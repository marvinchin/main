package seedu.address.ui;

import java.util.HashMap;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.ReadOnlyPerson;


/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";
    private static final String[] COLORS = {"Crimson", "orange", "DarkSalmon", "LightSeaGreen",
        "RoyalBlue", "MediumPurple", "Teal", "Sienna", "HotPink", "MediumSeaGreen",
        "DarkSlateBlue"};
    private static final int NUM_COLORS = COLORS.length;
    private static int colorIndex = 0;
    public final ReadOnlyPerson person;
    private HashMap<String, String> tagColors = new HashMap<String, String>();

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */


    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
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
    private Label timetable;
    @FXML
    private Label remark;
    @FXML
    private Label photoPath;
    @FXML
    private FlowPane tags;

    public PersonCard(ReadOnlyPerson person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        initTags(person);
        bindListeners(person);
    }

    /**
     * Gets a random unused color for the new tagName, or returns the corresponding color of the old tagName
     * @param tagName
     * @return the color of the tag
     */
    private String getTagColor(String tagName) {
        if (!tagColors.containsKey(tagName)) {
            tagColors.put(tagName, COLORS[colorIndex]);
            updateColorIndex();
        }
        return tagColors.get(tagName);
    }

    /**
     * Updates the color index to pick a new color for the new tag.
     */
    private static void updateColorIndex() {
        if (colorIndex == NUM_COLORS - 1) {
            colorIndex = 0;
        } else {
            colorIndex++;
        }
    }

    /**
     * Binds the individual UI elements to observe their respective {@code Person} properties
     * so that they will be notified of any changes.
     */
    private void bindListeners(ReadOnlyPerson person) {
        name.textProperty().bind(Bindings.convert(person.nameProperty()));
        gender.textProperty().bind(Bindings.convert(person.genderProperty()));
        matricNo.textProperty().bind(Bindings.convert(person.matricNoProperty()));
        phone.textProperty().bind(Bindings.convert(person.phoneProperty()));
        address.textProperty().bind(Bindings.convert(person.addressProperty()));
        email.textProperty().bind(Bindings.convert(person.emailProperty()));
        birthday.textProperty().bind(Bindings.convert(person.birthdayProperty()));
        timetable.textProperty().bind(Bindings.convert(person.timetableProperty()));
        remark.textProperty().bind(Bindings.convert(person.remarkProperty()));
        photoPath.textProperty().bind(Bindings.convert(person.photoPathProperty()));
        person.tagProperty().addListener((observable, oldValue, newValue) -> {
            tags.getChildren().clear();
            initTags(person);
        });
    }

    /**
     * Initializes the tags for person list
     * @param person
     */
    private void initTags(ReadOnlyPerson person) {
        person.getTags().forEach(tag -> {
                Label tagLabel = new Label(tag.tagName);
                tagLabel.setStyle("-fx-background-color: " + getTagColor(tag.tagName));
                tags.getChildren().add(tagLabel);
            }
        );
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonCard)) {
            return false;
        }

        // state check
        PersonCard card = (PersonCard) other;
        return id.getText().equals(card.id.getText())
                && person.equals(card.person);
    }
}
