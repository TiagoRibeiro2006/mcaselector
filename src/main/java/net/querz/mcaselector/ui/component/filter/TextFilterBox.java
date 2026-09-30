package net.querz.mcaselector.ui.component.filter;

import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import net.querz.mcaselector.filter.Comparator;
import net.querz.mcaselector.filter.Filter;
import net.querz.mcaselector.filter.TextFilter;
import net.querz.mcaselector.filter.filters.StructureFilter;
import net.querz.mcaselector.text.Translation;
import net.querz.mcaselector.ui.UIFactory;
import net.querz.mcaselector.version.mapping.registry.StructureRegistry;
import java.util.Objects;

public class TextFilterBox extends FilterBox {

	protected TextInputControl input;
	private final ComboBox<Comparator> comparator = new ComboBox<>();

	private static final PseudoClass invalid = PseudoClass.getPseudoClass("invalid");
	private static final String stylesheet = Objects.requireNonNull(TextFilterBox.class.getClassLoader().getResource("style/component/text-filter-box.css")).toExternalForm();

	public TextFilterBox(FilterBox parent, TextFilter<?> filter, boolean root) {
		super(parent, filter, root);
		getStyleClass().add("text-filter-box");
		ComboBox<String> knownValuePicker = createStructurePicker(filter);
		if (knownValuePicker == null) {
			input = new javafx.scene.control.TextField();
			setCenter(input);
		} else {
			input = knownValuePicker.getEditor();
			setCenter(knownValuePicker);
		}
		input.setPromptText(filter.getFormatText());
		input.textProperty().addListener((a, b, c) -> onTextInput(filter, c));
		if (input instanceof javafx.scene.control.TextField textField) {
			textField.setAlignment(Pos.CENTER);
		}

		comparator.setTooltip(UIFactory.tooltip(Translation.DIALOG_FILTER_CHUNKS_FILTER_COMPARATOR_TOOLTIP));
		comparator.getItems().addAll(filter.getComparators());
		comparator.getSelectionModel().select(filter.getComparator());
		comparator.setOnAction(e -> onComparator(filter));

		comparator.getStyleClass().add("filter-comparator-combo-box");

		filterOperators.add(comparator, 2, 0, 1, 1);

		setText(filter.getRawValue());
		onTextInput(filter, filter.getRawValue());

		getStylesheets().add(stylesheet);
	}

	public void setText(String text) {
		input.setText(text);
	}

	private ComboBox<String> createStructurePicker(TextFilter<?> filter) {
		if (!(filter instanceof StructureFilter)) {
			return null;
		}

		ComboBox<String> knownValues = new ComboBox<>();
		StructureRegistry.forEachDisplayName((displayName, structure) -> knownValues.getItems().add(structure.id()));
		knownValues.setEditable(true);
		knownValues.setMaxWidth(Double.MAX_VALUE);
		knownValues.getStyleClass().add("filter-known-value-combo-box");
		knownValues.setOnAction(e -> {
			String value = knownValues.getValue();
			if (value == null) {
				return;
			}
			knownValues.getEditor().setText(value);
		});
		return knownValues;
	}

	private void onTextInput(Filter<?> filter, String newValue) {
		filter.setFilterValue(newValue);
		pseudoClassStateChanged(invalid, !filter.isValid());
		callUpdateEvent();
	}

	private void onComparator(TextFilter<?> filter) {
		filter.setComparator(comparator.getSelectionModel().getSelectedItem());
		callUpdateEvent();
	}
}
