# zacharytang
###### \java\seedu\address\commons\events\ui\PersonSelectedEvent.java
``` java
/**
 * Represents a person being selected
 */
public class PersonSelectedEvent extends BaseEvent {

    public final ReadOnlyPerson person;

    public PersonSelectedEvent(ReadOnlyPerson person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "Person selected: " + person.getName().toString();
    }
}
```
###### \java\seedu\address\commons\util\timetable\Lesson.java
``` java
/**
 * Represents a lesson that a module has
 */
public class Lesson {

    private final String classNo;
    private final String lessonType;
    private final String weekType;
    private final String day;
    private final String startTime;
    private final String endTime;

    public Lesson(String classNo, String lessonType, String weekType,
                  String day, String startTime, String endTime) {
        this.classNo = classNo;
        this.lessonType = lessonType;
        this.weekType = weekType;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getClassNo() {
        return classNo;
    }

    public String getLessonType() {
        return lessonType;
    }

    public String getWeekType() {
        return weekType;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
```
###### \java\seedu\address\commons\util\timetable\ModuleInfoFromUrl.java
``` java
/**
 * Represents lesson for a specific module parsed from a NUSMods url
 */
public class ModuleInfoFromUrl {

    private String modCode;
    private HashMap<String, String> lessonInfo;

    public ModuleInfoFromUrl(String modCode) {
        this.modCode = modCode;
        lessonInfo = new HashMap<>();
    }

    /**
     * Adds information for a specific lesson
     */
    public void addLesson(String classType, String classNo) {
        lessonInfo.put(classType, classNo);

    }

    public String getModCode() {
        return modCode;
    }

    public HashMap<String, String> getLessonInfo() {
        return lessonInfo;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {

            return true;
        }

        if (!(other instanceof ModuleInfoFromUrl)) {
            return false;
        }

        return this.modCode.equals(((ModuleInfoFromUrl) other).getModCode());
    }
}
```
###### \java\seedu\address\commons\util\timetable\TimetableInfoFromUrl.java
``` java
/**
 * Represents all timetable information parsed from a NUSMods url
 */
public class TimetableInfoFromUrl {

    private ArrayList<ModuleInfoFromUrl> moduleInfo;

    public TimetableInfoFromUrl() {
        moduleInfo = new ArrayList<>();
    }

    public ArrayList<ModuleInfoFromUrl> getModuleInfoList() {
        return moduleInfo;
    }

    /**
     * Gets lessons for a specific module code. If module does not exist, creates a new ModuleInfoFromUrl object
     */
    public ModuleInfoFromUrl getModuleInfo(String moduleCodeToGet) {
        for (ModuleInfoFromUrl currentModule : moduleInfo) {
            if (currentModule.getModCode().equals(moduleCodeToGet)) {
                return currentModule;
            }
        }

        return new ModuleInfoFromUrl(moduleCodeToGet);
    }

    /**
     * Adds lesson information for a specific module to the timetable information.
     */
    public void addModuleInfo(ModuleInfoFromUrl moduleInfoToAdd) {
        if (moduleInfo.contains(moduleInfoToAdd)) {
            moduleInfo.set(moduleInfo.indexOf(moduleInfoToAdd), moduleInfoToAdd);
        } else {
            moduleInfo.add(moduleInfoToAdd);
        }
    }
}
```
###### \java\seedu\address\commons\util\timetable\TimetableParserUtil.java
``` java
/**
 * Helper class that contains utilities to parse NUSMods urls.
 */
public class TimetableParserUtil {

    public static final String MESSAGE_INVALID_CLASS_TYPE = "Invalid class type provided!";
    public static final String MESSAGE_INVALID_WEEK_TYPE = "Invald week type!";
    public static final String MESSAGE_INVALID_TIME = "Invalid timing provided!";
    public static final String MESSAGE_INVALID_DAY = "Invalid day provided";

    private static final int INDEX_ACAD_YEAR = 4;
    private static final int INDEX_MODULE_INFO = 5;
    private static final int INDEX_SEMESTER_INFO = 0;
    private static final int INDEX_CLASS_INFO = 1;

    private static final String SPLIT_BACKWARDS_SLASH = "/";
    private static final String SPLIT_QUESTION_MARK = "\\?";
    private static final String SPLIT_AMPERSAND = "&";
    private static final String SPLIT_EQUALS_SIGN = "=";
    private static final String SPLIT_LEFT_SQAURE_BRACKET = "%5B";
    private static final String SPLIT_RIGHT_SQUARE_BRACKET = "%5D";

    /**
     * Takes in a valid timetable URL and attempts to parse it
     */
    public static TimetableInfo parseUrl(String url) throws IllegalValueException {
        if (!isValidUrl(url)) {
            throw new IllegalValueException(MESSAGE_TIMETABLE_URL_CONSTRAINTS);
        }

        try {
            String newUrl = expandUrl(url);
            return parseLongUrl(newUrl);
        } catch (IOException e) {
            throw new ParseException("Url cannot be accessed");
        }
    }

    /**
     * Parses a full expanded NUSMods url
     */
    private static TimetableInfo parseLongUrl(String url) throws IllegalValueException {
        String acadYear;
        String semester;

        String[] splitUrl = url.split(SPLIT_BACKWARDS_SLASH);

        acadYear = splitUrl[INDEX_ACAD_YEAR];
        String toParse = splitUrl[INDEX_MODULE_INFO];
        String[] modInfo = toParse.split(SPLIT_QUESTION_MARK);

        if (modInfo.length != 2) {
            return new TimetableInfo();
        }

        semester = modInfo[INDEX_SEMESTER_INFO].substring(3);

        TimetableInfoFromUrl timetableInfo = parseModuleInfo(modInfo[INDEX_CLASS_INFO]);

        return constructTimetable(acadYear, semester, timetableInfo);
    }

    /**
     * Parses a string of module info from an expanded NUSMods link
     * Returns a hashmap where module code is the key, and values are a hashmap of class types and class numbers
     */
    private static TimetableInfoFromUrl parseModuleInfo(String modInfoFromString) {
        TimetableInfoFromUrl modules = new TimetableInfoFromUrl();
        String[] classes = modInfoFromString.split(SPLIT_AMPERSAND);

        // Split a string of format "MODULE_CODE[CLASS_TYPE]=CLASS_NO"
        for (String classInfo : classes) {
            String moduleCode = classInfo.split(SPLIT_LEFT_SQAURE_BRACKET)[0];
            String classType = classInfo.split(SPLIT_LEFT_SQAURE_BRACKET)[1].split(SPLIT_RIGHT_SQUARE_BRACKET)[0];
            String classNo = classInfo.split(SPLIT_EQUALS_SIGN)[1];

            ModuleInfoFromUrl moduleInfo = modules.getModuleInfo(moduleCode);
            moduleInfo.addLesson(classType, classNo);
            modules.addModuleInfo(moduleInfo);
        }

        return modules;
    }

    /**
     * Uses NUSMods API to obtain all classes a module has, and returns it in
     * an arraylist of classes. Each class is represented by a hash map, storing the information about the class
     * @return list of all lessons a module has
     */
    private static ArrayList<Lesson> getLessonInfoFromApi(String acadYear, String semester, String modCode)
            throws ParseException {
        String uri = "http://api.nusmods.com/" + acadYear + "/" + semester + "/modules/" + modCode + ".json";
        ObjectMapper mapper = new ObjectMapper();

        try {
            URL url = new URL(uri);
            @SuppressWarnings("unchecked")
            Map<String, Object> mappedJson = mapper.readValue(url, HashMap.class);
            @SuppressWarnings("unchecked")
            ArrayList<HashMap<String, String>> lessonInfo = (ArrayList<HashMap<String, String>>)
                    mappedJson.get("Timetable");

            ArrayList<Lesson> lessons = new ArrayList<>();
            for (HashMap<String, String> lesson : lessonInfo) {
                Lesson lessonToAdd = new Lesson(lesson.get("ClassNo"), lesson.get("LessonType"),
                        lesson.get("WeekText"), lesson.get("DayText"),
                        lesson.get("StartTime"), lesson.get("EndTime"));
                lessons.add(lessonToAdd);
            }

            return lessons;
        } catch (Exception e) {
            throw new ParseException("Cannot retrieve module information");
        }
    }

    /**
     * Takes a shortened URL and returns the full length URL as a string
     */
    private static String expandUrl(String shortenedUrl) throws IOException, ParseException {
        URL url = new URL(shortenedUrl);
        // open connection
        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);

        // stop following browser redirect
        httpUrlConnection.setInstanceFollowRedirects(false);

        // extract location header containing the actual destination URL
        String expandedUrl = httpUrlConnection.getHeaderField("Location");
        httpUrlConnection.disconnect();

        if (expandedUrl.equals("http://modsn.us")) {
            throw new ParseException(MESSAGE_INVALID_SHORT_URL);
        }

        return expandedUrl;
    }

    /**
     * Constructs a timetable array using information parsed from a url
     * @param acadYear academic year
     * @param semester semester
     * @param timetableInfo Class information parsed from url, stored by module code
     */
    private static TimetableInfo constructTimetable(String acadYear, String semester,
                                                    TimetableInfoFromUrl timetableInfo) throws IllegalValueException {
        TimetableInfo timetable = new TimetableInfo();

        ArrayList<ModuleInfoFromUrl> lessonInfoByModules = timetableInfo.getModuleInfoList();

        for (ModuleInfoFromUrl moduleInfoFromTimetable : lessonInfoByModules) {
            constructTimetableForModule(acadYear, semester, timetable, moduleInfoFromTimetable);
        }

        return timetable;
    }

    /**
     * Adds all lessons for a specific module found in the timetable information parsed from URL
     * Timings for every lesson from a specific module will be added to the timetable information
     * @param acadYear academic year
     * @param semester semester
     * @param timetable timetable to be updated
     * @param moduleInfo lessons for a module parsed from url
     */
    private static void constructTimetableForModule(String acadYear, String semester, TimetableInfo timetable,
                                                    ModuleInfoFromUrl moduleInfo) throws IllegalValueException {
        ArrayList<Lesson> lessons = getLessonInfoFromApi(acadYear, semester, moduleInfo.getModCode());
        HashMap<String, String> lessonsForModule = moduleInfo.getLessonInfo();

        for (String classType : lessonsForModule.keySet()) {
            String classNo = lessonsForModule.get(classType);

            addLessonToTimetable(timetable, lessons, classType, classNo);
        }
    }

    /**
     * Finds the timings for a lesson in the URL parsed info, and adds it to the timetable
     * @param timetable to be returned
     * @param lessons list of all lessons for a module
     * @param classType type of class parsed
     * @param classNo identifier for class parsed
     */
    private static void addLessonToTimetable(TimetableInfo timetable, ArrayList<Lesson> lessons, String classType,
                                             String classNo) throws IllegalValueException {
        for (Lesson lesson : lessons) {
            if (lessonExistsInParsedInfo(classType, classNo, lesson)) {
                timetable.updateSlotsWithLesson(lesson.getWeekType(), lesson.getDay(), lesson.getStartTime(),
                        lesson.getEndTime());
            }
        }
    }

    /**
     * Checks if lesson info that was parsed from URL is equivalent to a lesson
     */
    private static boolean lessonExistsInParsedInfo(String classType, String classNo, Lesson lesson)
            throws IllegalValueException {
        return parseSlotType(classType).equals(lesson.getLessonType()) && classNo.equals(lesson.getClassNo());
    }

    /* ------------------------------------------- Helper methods ----------------------------------------------------*/

    /**
     * Converts string representing day of class to integer representation
     * Returns -1 if day cannot be found;
     */
    public static int parseDay(String day) throws IllegalValueException {
        switch (day) {
        case "Monday":
            return DAY_MONDAY;

        case "Tuesday":
            return DAY_TUESDAY;

        case "Wednesday":
            return DAY_WEDNESDAY;

        case "Thursday":
            return DAY_THURSDAY;

        case "Friday":
            return DAY_FRIDAY;

        default:
            throw new IllegalValueException(MESSAGE_INVALID_DAY);
        }
    }

    /**
     * Takes in String representing start/end timing of lessons, and returns respective index to be used for array
     */
    public static int parseStartEndTime(String timing) throws IllegalValueException {
        try {
            return (int) Math.ceil(((Integer.parseInt(timing) - 800) * 2) / 100.0);
        } catch (NumberFormatException e) {
            throw new IllegalValueException(MESSAGE_INVALID_TIME);
        }
    }

    /**
     * Converts shortened slot type in URL to full slot-type string used in API
     */
    public static String parseSlotType(String slotType) throws IllegalValueException {
        switch (slotType) {
        case "LEC":
            return "Lecture";

        case "TUT":
            return "Tutorial";

        case "LAB":
            return "Laboratory";

        case "SEM":
            return "Seminar-Style Module Class";

        case "SEC":
            return "Sectional Teaching";

        case "REC":
            return "Recitation";

        default:
            throw new IllegalValueException(MESSAGE_INVALID_CLASS_TYPE);
        }
    }

    /**
     * Converts week type from string used in api to integer index for use in URL
     */
    public static int parseWeekType(String weekType) throws IllegalValueException {
        switch (weekType) {
        case "Odd Week":
            return WEEK_ODD;
        case "Even Week":
            return WEEK_EVEN;
        case "Every Week":
            return WEEK_BOTH;
        default:
            throw new IllegalValueException(MESSAGE_INVALID_WEEK_TYPE);
        }
    }
}
```
###### \java\seedu\address\model\person\timetable\Timetable.java
``` java
/**
 * Represents a person's timetable in the address book
 * Guarantees: Immutable
 */
public class Timetable {

    public static final int WEEK_ODD = 0;
    public static final int WEEK_EVEN = 1;
    public static final int WEEK_BOTH = -1;

    public static final int DAY_MONDAY = 0;
    public static final int DAY_TUESDAY = 1;
    public static final int DAY_WEDNESDAY = 2;
    public static final int DAY_THURSDAY = 3;
    public static final int DAY_FRIDAY = 4;

    public static final String MESSAGE_TIMETABLE_URL_CONSTRAINTS =
            "Timetable URLs should be a valid shortened NUSMods URL";
    public static final String MESSAGE_INVALID_SHORT_URL = "Invalid shortened URL provided";

    public static final String[] ARRAY_DAYS = {
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday"
    };

    public static final String[] ARRAY_WEEKS = {
        "Odd Week",
        "Even Week"
    };

    public static final String[] ARRAY_TIMES = {
        "0800", "0830", "0900", "0930", "1000", "1030", "1100", "1130",
        "1200", "1230", "1300", "1330", "1400", "1430", "1500", "1530",
        "1600", "1630", "1700", "1730", "1800", "1830", "1900", "1930",
        "2000", "2030", "2100", "2130", "2200", "2230", "2300", "2330"
    };

    private static final String NUSMODS_SHORT = "modsn.us";
    private static final String URL_HOST_REGEX = "\\/\\/.*?\\/";

    public final String value;

    private final TimetableInfo timetable;

    public Timetable(String url) throws IllegalValueException {
        requireNonNull(url);
        String trimmedUrl = url.trim();
        if (!isValidUrl(trimmedUrl)) {
            throw new IllegalValueException(MESSAGE_TIMETABLE_URL_CONSTRAINTS);
        }
        this.value = trimmedUrl;
        this.timetable = parseUrl(trimmedUrl);
    }

    /**
     * Returns if a url is a valid NUSMods url
     */
    public static boolean isValidUrl(String test) {
        Matcher matcher = Pattern.compile(URL_HOST_REGEX).matcher(test);
        if (!matcher.find()) {
            return false;
        }

        String hostName = matcher.group()
                .substring(2, matcher.group().length() - 1);

        return hostName.equals(NUSMODS_SHORT);
    }

    /**
     * Checks if a timeslot specified has a lesson
     */
    public boolean doesSlotHaveLesson(String weekType, String day, String timing) throws IllegalValueException {
        return timetable.doesSlotHaveLesson(weekType, day, timing);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Timetable // instanceof handles nulls
                && this.value.equals(((Timetable) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
```
###### \java\seedu\address\model\person\timetable\TimetableDay.java
``` java
/**
 * Represents a single day in a timetable
 */
public class TimetableDay {

    private static final int ARRAY_NUM_TIMESLOTS = 32;

    private TimetableSlot[] slots;

    public TimetableDay() {
        slots = new TimetableSlot[ARRAY_NUM_TIMESLOTS];
        for (int i = 0; i < ARRAY_NUM_TIMESLOTS; i++) {
            slots[i] = new TimetableSlot();
        }
    }

    /**
     * Sets all slots between two timings to have lessons
     */
    public void updateSlotsWithLesson(String startTime, String endTime) throws IllegalValueException {
        int startTimeIndex = parseStartEndTime(startTime);
        int endTimeIndex = parseStartEndTime(endTime);

        for (int i = startTimeIndex; i < endTimeIndex; i++) {
            slots[i].setLesson();
        }
    }

    public boolean doesSlotHaveLesson(String timing) throws IllegalValueException {
        return slots[parseStartEndTime(timing)].hasLesson();
    }

    private TimetableSlot getTimetableSlot(int index) {
        return slots[index];
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TimetableDay)) {
            return false;
        }

        for (int i = 0; i < ARRAY_NUM_TIMESLOTS; i++) {
            if (!this.slots[i].equals(((TimetableDay) other).getTimetableSlot(i))) {
                return false;
            }
        }

        return true;
    }
}
```
###### \java\seedu\address\model\person\timetable\TimetableInfo.java
``` java
/**
 * Fully represents all information about a person's timetable slots
 */
public class TimetableInfo {

    public static final int ARRAY_NUM_EVEN_ODD = 2;

    private TimetableWeek[] timetable;

    public TimetableInfo() {
        timetable = new TimetableWeek[ARRAY_NUM_EVEN_ODD];
        for (int i = 0; i < ARRAY_NUM_EVEN_ODD; i++) {
            timetable[i] = new TimetableWeek();
        }
    }

    /**
     * Checks if a specific slot has a lesson
     */
    public boolean doesSlotHaveLesson(String weekType, String day, String timing) throws IllegalValueException {
        int weekIndex = parseWeekType(weekType);
        if (weekIndex != WEEK_ODD && weekIndex != WEEK_EVEN) {
            throw new IllegalValueException("Please specify a week type!");
        }
        return timetable[weekIndex].doesSlotHaveLesson(day, timing);
    }

    /**
     * Updates the timetable using lesson information provided
     */
    public void updateSlotsWithLesson(String weekType, String day, String startTime, String endTime)
            throws IllegalValueException {
        int weekIndex = parseWeekType(weekType);
        if (weekIndex == WEEK_BOTH) {
            timetable[WEEK_ODD].updateSlotsWithLesson(day, startTime, endTime);
            timetable[WEEK_EVEN].updateSlotsWithLesson(day, startTime, endTime);
        } else {
            timetable[weekIndex].updateSlotsWithLesson(day, startTime, endTime);
        }
    }

    private TimetableWeek getWeek(int weekIndex) {
        return timetable[weekIndex];
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TimetableInfo)) {
            return false;
        }

        for (int i = 0; i < ARRAY_NUM_EVEN_ODD; i++) {
            if (!this.timetable[i].equals(((TimetableInfo) other).getWeek(i))) {
                return false;
            }
        }

        return true;
    }
}
```
###### \java\seedu\address\model\person\timetable\TimetableSlot.java
``` java
/**
 * Represents a single slot in a timetable
 */
public class TimetableSlot {

    private boolean hasLesson;

    public TimetableSlot() {
        this.hasLesson = false;
    }

    public void setLesson() {
        hasLesson = true;
    }

    public boolean hasLesson() {
        return hasLesson;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TimetableSlot // instanceof handles nulls
                && this.hasLesson == ((TimetableSlot) other).hasLesson());
    }
}
```
###### \java\seedu\address\model\person\timetable\TimetableWeek.java
``` java
/**
 * Represents a full timetable for a week
 */
public class TimetableWeek {

    private static final int ARRAY_NUM_DAYS = 5;

    private TimetableDay[] days;

    public TimetableWeek() {
        days = new TimetableDay[ARRAY_NUM_DAYS];
        for (int i = 0; i < ARRAY_NUM_DAYS; i++) {
            days[i] = new TimetableDay();
        }
    }

    public boolean doesSlotHaveLesson(String day, String timing) throws IllegalValueException {
        return days[parseDay(day)].doesSlotHaveLesson(timing);
    }

    public void updateSlotsWithLesson(String day, String startTime, String endTime) throws IllegalValueException {
        days[parseDay(day)].updateSlotsWithLesson(startTime, endTime);
    }

    private TimetableDay getDay(int dayIndex) {
        return days[dayIndex];
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TimetableWeek)) {
            return false;
        }

        for (int i = 0; i < ARRAY_NUM_DAYS; i++) {
            if (!this.days[i].equals(((TimetableWeek) other).getDay(i))) {
                return false;
            }
        }

        return true;
    }
}
```
###### \java\seedu\address\ui\InfoPanel.java
``` java
/**
 * Container for both browser panel and person information panel
 */
public class InfoPanel extends UiPart<Region> {

    private static final String FXML = "InfoPanel.fxml";

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    private BrowserPanel browserPanel;
    private PersonInfoOverview infoOverview;

    @FXML
    private StackPane browserPlaceholder;

    @FXML
    private StackPane infoOverviewPlaceholder;


    public InfoPanel() {
        super(FXML);

        registerAsAnEventHandler(this);
        browserPanel = new BrowserPanel();
        browserPlaceholder.getChildren().add(browserPanel.getRoot());

        infoOverview = new PersonInfoOverview();
        infoOverviewPlaceholder.getChildren().add(infoOverview.getRoot());
    }

    public void freeResources() {
        browserPanel.freeResources();
    }

    @Subscribe
    public void handlePersonSelectedEvent(PersonSelectedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        infoOverviewPlaceholder.toFront();
    }

    @Subscribe
    public void handlePersonAddressDisplayDirectionsEvent(PersonAddressDisplayDirectionsEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        browserPlaceholder.toFront();
    }

    @Subscribe
    public void handlePersonAddressDisplayMapEvent(PersonAddressDisplayMapEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        browserPlaceholder.toFront();
    }
}
```
###### \java\seedu\address\ui\PersonInfoOverview.java
``` java
/**
 * A UI component that displays a person's data on the main panel
 */
public class PersonInfoOverview extends UiPart<Region> {

    private static final String FXML = "PersonInfoOverview.fxml";
    private ReadOnlyPerson person;

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    private TimetableDisplay timetableDisplay;

    @FXML
    private Label name;
    @FXML
    private Label gender;
    @FXML
    private Label matricNo;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label birthday;
    @FXML
    private Label remark;

    @FXML
    private AnchorPane timetablePlaceholder;

    public PersonInfoOverview() {
        super(FXML);

        this.person = null;
        loadDefaultPerson();

        registerAsAnEventHandler(this);
    }

    /**
     * Loads the default person when the app is first started
     */
    private void loadDefaultPerson() {
        name.setText("No person selected");
        gender.setText("");
        matricNo.setText("");
        phone.setText("");
        address.setText("");
        email.setText("");
        birthday.setText("");
        remark.setText("");

        timetableDisplay = new TimetableDisplay(null);
        timetablePlaceholder.getChildren().add(timetableDisplay.getRoot());
    }

    /**
     * Updates info with person selected
     */
    private void loadPerson(ReadOnlyPerson person) {
        this.person = person;

        name.textProperty().bind(Bindings.convert(person.nameProperty()));
        gender.textProperty().bind(Bindings.convert(person.genderProperty()));
        matricNo.textProperty().bind(Bindings.convert(person.matricNoProperty()));
        phone.textProperty().bind(Bindings.convert(person.phoneProperty()));
        address.textProperty().bind(Bindings.convert(person.addressProperty()));
        email.textProperty().bind(Bindings.convert(person.emailProperty()));
        birthday.textProperty().bind(Bindings.convert(person.birthdayProperty()));
        remark.textProperty().bind(Bindings.convert(person.remarkProperty()));

        timetablePlaceholder.getChildren().removeAll();

        timetableDisplay = new TimetableDisplay(person.getTimetable());
        timetablePlaceholder.getChildren().add(timetableDisplay.getRoot());
    }

    @Subscribe
    private void handlePersonSelectedEvent(PersonSelectedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadPerson(event.person);
    }

    @Subscribe
    private void handlePersonPanelSelectionChangeEvent(PersonPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadPerson(event.getNewSelection().person);
    }
}
```
###### \java\seedu\address\ui\TimetableDisplay.java
``` java
/**
 * Display for timetables in the UI
 */
public class TimetableDisplay extends UiPart<Region> {

    private static final String COLOUR_FILLED = "#515658";
    private static final String COLOUR_EMPTY = "#383838";
    private static final String COLOUR_BORDER = "#000000";

    private static final String FXML = "TimetableDisplay.fxml";
    private Timetable timetable;

    @FXML
    private GridPane oddGrid;

    @FXML
    private GridPane evenGrid;

    public TimetableDisplay(Timetable timetable) {
        super(FXML);

        this.timetable = timetable;
        fillTimetable();
    }

    /**
     * Fills the timetable grid with panes according to the timetable oject given
     */
    private void fillTimetable() {

        for (int weekType = 0; weekType < ARRAY_WEEKS.length; weekType++) {
            for (int day = 0; day < ARRAY_DAYS.length; day++) {
                for (int time = 0; time < ARRAY_TIMES.length; time++) {
                    markSlot(weekType, day, time, timetable != null ? hasLesson(weekType, day, time) : false);
                }
            }
        }
    }

    /**
     * Fills a slot in the timetable grid with a lesson
     */
    private void markSlot(int weekIndex, int dayIndex, int timeIndex, boolean hasLesson) {

        Pane pane = new Pane();
        GridPane.setColumnIndex(pane, timeIndex + 1);
        GridPane.setRowIndex(pane, dayIndex + 1);

        // Sets the borders such that half hour slots are combined into an hour
        String borderStyle = timeIndex % 2 == 0 ? "solid none solid solid" : "solid solid solid none";

        // Fixes the widths to be even across the grid
        String topWidth = dayIndex == 0 ? "2" : "1";
        String leftWidth = timeIndex == 0 ? "2" : "1";
        String bottomWidth = dayIndex == 4 ? "2" : "1";
        String rightWidth = timeIndex == 31 ? "2" : "1";
        String borderWidths = topWidth + " " + rightWidth + " " + bottomWidth + " " + leftWidth;

        pane.setStyle("-fx-background-color: " + (hasLesson ? COLOUR_FILLED : COLOUR_EMPTY)
                + ";\n"
                + "-fx-border-color: " + COLOUR_BORDER + ";\n"
                + "-fx-border-width: " + borderWidths + ";\n"
                + "-fx-border-style: " + borderStyle);

        if (weekIndex == WEEK_ODD) {
            oddGrid.getChildren().add(pane);
        } else {
            evenGrid.getChildren().add(pane);
        }
    }

    /**
     * Checks if a lesson exists in the timetable at the given parameters
     */
    private boolean hasLesson(int weekIndex, int dayIndex, int timeIndex) {
        try {
            return timetable.doesSlotHaveLesson(ARRAY_WEEKS[weekIndex], ARRAY_DAYS[dayIndex], ARRAY_TIMES[timeIndex]);
        } catch (IllegalValueException e) {
            e.printStackTrace();
            return false;
        }
    }
}
```
###### \resources\view\DarkTheme.css
``` css
.split-pane:horizontal .split-pane-divider {
    -fx-background-color: derive(#1d1d1d, 20%);
    -fx-border-color: transparent transparent transparent transparent;
}

.split-pane:vertical .split-pane-divider {
    -fx-background-color: derive(#1d1d1d, 20%);
    -fx-border-color: #4d4d4d transparent transparent transparent;
}

.split-pane {
    -fx-border-radius: 0;
    -fx-border-width: 0;
    -fx-background-color: derive(#1d1d1d, 20%);
}

.info-panel {
    -fx-border-radius: 2;
    -fx-border-width: 2;
    -fx-border-color: #4d4d4d;
}

.browser-panel {
    -fx-border-radius: 2;
    -fx-border-width: 2;
    -fx-border-color: #4d4d4d;
}

```
###### \resources\view\DarkTheme.css
``` css
.display_big_label {
    -fx-font-family: "Segoe UI";
    -fx-font-size: 24px;
    -fx-font-weight: bold;
    -fx-text-fill: #ffffff;
}

.display_small_label {
    -fx-font-family: "Segoe UI Semibold";
    -fx-font-size: 15px;
    -fx-text-fill: #ffffff;
}

.display_small_value {
    -fx-font-family: "Segoe UI";
    -fx-font-size: 14px;
    -fx-text-fill: #ffffff;
}

.timetable-label {
    -fx-font-family: "Segoe UI Semibold";
    -fx-font-weight: 500;
    -fx-text-fill: #FFFFFF;
}

.tab-pane .tab-header-area .tab-header-background {
    -fx-opacity: 0;
}

.tab-pane .tab {
    -fx-background-color: #383838;
}

.tab-pane .tab:selected {
    -fx-background-color: #515658;
}

.tab .tab-label {
    -fx-alignment: CENTER;
    -fx-text-fill: #FFFFFF;
    -fx-font-size: 12px;
    -fx-font-family: "Segoe UI";
}

.tab:selected .tab-label {
    -fx-alignment: CENTER;
    -fx-font-family: "Segoe UI Semibold";
    -fx-text-fill: #FFFFFF;
}

.info-name-cell {
    -fx-background-color: #515658;
}

.info-cell {
    -fx-background-color: #3c3e3f;
}

```
###### \resources\view\InfoPanel.fxml
``` fxml
<StackPane prefHeight="600.0" prefWidth="800.0" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
    <StackPane fx:id="browserPlaceholder" prefHeight="150.0" prefWidth="200.0" />
    <StackPane fx:id="infoOverviewPlaceholder" prefHeight="150.0" prefWidth="200.0" />
</StackPane>
```
###### \resources\view\MainWindow.fxml
``` fxml
    <StackPane fx:id="infoPlaceholder" prefWidth="340" styleClass="pane-with-border">
      <padding>
        <Insets bottom="10" left="10" right="10" top="10" />
      </padding>
    </StackPane>
```
###### \resources\view\PersonInfoOverview.fxml
``` fxml
<StackPane styleClass="info-panel" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
    <!--Edit dividerPositions below to adjust vertical divider position between the person info and the timetable area -->
    <SplitPane dividerPositions="0.3" orientation="VERTICAL">
        <AnchorPane>
            <!--Edit dividerPositions below to adjust the divider position between the person photo and the person details-->
            <SplitPane dividerPositions="0.5" AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0">
                <AnchorPane minHeight="0.0" minWidth="0.0" styleClass="info-name-cell">
                    <Label fx:id="name" alignment="CENTER" contentDisplay="CENTER" prefHeight="28.0" prefWidth="43.2" styleClass="display_big_label" text="\$name" textAlignment="CENTER" AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0" />
                </AnchorPane>
                <AnchorPane minHeight="0.0" minWidth="0.0" styleClass="info-cell">
                    <VBox alignment="CENTER_LEFT" AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="20.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="0.0">
                        <HBox alignment="CENTER_LEFT" spacing="5">
                            <Label styleClass="display_small_label" text="Gender:" />
                            <Label fx:id="gender" styleClass="display_small_value" text="\$gender" />
                        </HBox>
                        <HBox alignment="CENTER_LEFT" spacing="5">
                            <Label styleClass="display_small_label" text="Matriculation No:" />
                            <Label fx:id="matricNo" styleClass="display_small_value" text="\$matricNo" />
                        </HBox>
                        <HBox alignment="CENTER_LEFT" spacing="5">
                            <Label styleClass="display_small_label" text="Phone No:" />
                            <Label fx:id="phone" styleClass="display_small_value" text="\$phone" />
                        </HBox>
                        <HBox alignment="CENTER_LEFT" spacing="5">
                            <Label styleClass="display_small_label" text="Address:" />
                            <Label fx:id="address" styleClass="display_small_value" text="\$address" />
                        </HBox>
                        <HBox alignment="CENTER_LEFT" spacing="5">
                            <Label styleClass="display_small_label" text="Email:" />
                            <Label fx:id="email" styleClass="display_small_value" text="\$email" />
                        </HBox>
                        <HBox alignment="CENTER_LEFT" spacing="5">
                            <Label styleClass="display_small_label" text="Birthday:" />
                            <Label fx:id="birthday" styleClass="display_small_value" text="\$birthday" />
                        </HBox>
                        <HBox alignment="CENTER_LEFT" spacing="5">
                            <Label styleClass="display_small_label" text="Remark:" />
                            <Label fx:id="remark" styleClass="display_small_value" text="\$remark" />
                        </HBox>
                    </VBox>
                </AnchorPane>
            </SplitPane>
        </AnchorPane>
        <AnchorPane fx:id="timetablePlaceholder" />
    </SplitPane>
</StackPane>
```
###### \resources\view\TimetableDisplay.fxml
``` fxml
<AnchorPane AnchorPane.topAnchor="0.0" AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1" >
    <TabPane styleClass="timetable-tab-pane" layoutX="-1.600000023841858" layoutY="-2.4000000953674316" tabClosingPolicy="UNAVAILABLE"
             AnchorPane.bottomAnchor="0.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0"
             AnchorPane.topAnchor="0.0">
        <Tab fx:id="oddWeekTab" closable="false" text="Odd Week">
            <GridPane fx:id="oddGrid" alignment="CENTER">
                <columnConstraints>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="65.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                </columnConstraints>
                <rowConstraints>
                    <RowConstraints minHeight="10.0" prefHeight="10.0" vgrow="SOMETIMES"/>
                    <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES"/>
                    <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES"/>
                    <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES"/>
                    <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES"/>
                    <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES"/>
                </rowConstraints>
                <VBox alignment="CENTER" prefHeight="200.0" prefWidth="100.0" GridPane.rowIndex="5">
                    <Label styleClass="timetable-label" text="Fri"/>
                </VBox>
                <VBox alignment="CENTER" prefHeight="200.0" prefWidth="100.0" GridPane.rowIndex="1">
                    <Label styleClass="timetable-label" text="Mon"/>
                </VBox>
                <VBox alignment="CENTER" prefHeight="200.0" prefWidth="100.0" GridPane.rowIndex="4">
                    <Label styleClass="timetable-label" text="Thur"/>
                </VBox>
                <VBox alignment="CENTER" prefHeight="200.0" prefWidth="100.0" GridPane.rowIndex="3">
                    <Label styleClass="timetable-label" text="Wed"/>
                </VBox>
                <VBox alignment="CENTER" prefHeight="200.0" prefWidth="100.0" GridPane.rowIndex="2">
                    <Label styleClass="timetable-label" text="Tues"/>
                </VBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="1"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="0800"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="3"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="0900"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="5"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1000"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="7"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1100"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="9"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1200"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="11"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1300"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="13"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1400"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="15"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1500"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="17"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1600"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="19"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1700"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="21"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1800"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="23"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1900"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="25"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="2000"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="27"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="2100"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="29"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="2200"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="31"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="2300"/>
                </HBox>
                <padding>
                    <Insets bottom="10.0" left="0.0" right="10.0" top="0.0"/>
                </padding>
            </GridPane>
        </Tab>
        <Tab fx:id="evenWeekTab" closable="false" text="Even Week">
            <GridPane fx:id="evenGrid" alignment="CENTER">
                <columnConstraints>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="65.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                    <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="100.0"/>
                </columnConstraints>
                <rowConstraints>
                    <RowConstraints minHeight="10.0" prefHeight="10.0" vgrow="SOMETIMES"/>
                    <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES"/>
                    <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES"/>
                    <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES"/>
                    <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES"/>
                    <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES"/>
                </rowConstraints>
                <VBox alignment="CENTER" prefHeight="200.0" prefWidth="100.0" GridPane.rowIndex="5">
                    <Label styleClass="timetable-label" text="Fri"/>
                </VBox>
                <VBox alignment="CENTER" prefHeight="200.0" prefWidth="100.0" GridPane.rowIndex="1">
                    <Label styleClass="timetable-label" text="Mon"/>
                </VBox>
                <VBox alignment="CENTER" prefHeight="200.0" prefWidth="100.0" GridPane.rowIndex="4">
                    <Label styleClass="timetable-label" text="Thur"/>
                </VBox>
                <VBox alignment="CENTER" prefHeight="200.0" prefWidth="100.0" GridPane.rowIndex="3">
                    <Label styleClass="timetable-label" text="Wed"/>
                </VBox>
                <VBox alignment="CENTER" prefHeight="200.0" prefWidth="100.0" GridPane.rowIndex="2">
                    <Label styleClass="timetable-label" text="Tues"/>
                </VBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="1"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="0800"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="3"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="0900"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="5"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1000"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="7"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1100"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="9"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1200"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="11"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1300"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="13"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1400"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="15"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1500"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="17"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1600"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="19"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1700"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="21"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1800"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="23"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="1900"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="25"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="2000"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="27"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="2100"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="29"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="2200"/>
                </HBox>
                <HBox alignment="CENTER" prefHeight="100.0" prefWidth="200.0" GridPane.columnIndex="31"
                      GridPane.columnSpan="2">
                    <Label styleClass="timetable-label" text="2300"/>
                </HBox>
                <padding>
                    <Insets bottom="10.0" left="0.0" right="10.0" top="0.0"/>
                </padding>
            </GridPane>
        </Tab>
    </TabPane>
</AnchorPane>
```
