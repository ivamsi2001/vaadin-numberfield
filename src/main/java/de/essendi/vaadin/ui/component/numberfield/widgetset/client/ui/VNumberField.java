/* 
 * Copyright 2012 essendi it GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package de.essendi.vaadin.ui.component.numberfield.widgetset.client.ui;

import static de.essendi.vaadin.ui.component.numberfield.widgetset.shared.Constants.*;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.VTextField;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

import de.essendi.vaadin.ui.component.numberfield.widgetset.shared.NumberValidator;
import de.essendi.vaadin.ui.component.numberfield.widgetset.shared.NumberFieldAttributes;

/**
 * This client-side widget represents a basic input field for numerical values
 * (integers/decimals) and extends {@link VTextField}.
 */
public class VNumberField extends VTextField {

	private NumberFieldAttributes attributes = new NumberFieldAttributes();

	private KeyPressHandler keyPressHandler = new KeyPressHandler() {
		/**
		 * Do keystroke filtering (e.g. no letters) and validation for integer
		 * (123) and decimal numbers (12.3) on keypress events.
		 */
		public void onKeyPress(KeyPressEvent event) {
			if (isReadOnly() || !isEnabled()) {
				return;
			}

			int keyCode = event.getNativeEvent().getKeyCode();
			if (isControlKey(keyCode) || event.isAnyModifierKeyDown()) {
				return;
			}

			if (!isValueValid(event)) {
				cancelKey();
			}
		}

		private boolean isValueValid(KeyPressEvent event) {
			// Bypass the check that value >= attributes.getMinValue() on a
			// keypress
			double savedMinValue = attributes.getMinValue();
			attributes.setMinValue(Double.NEGATIVE_INFINITY);

			String newText = getFieldValueAsItWouldBeAfterKeyPress(event
					.getCharCode());
			boolean valueIsValid = attributes.isDecimalAllowed() ? NumberValidator
					.isValidDecimal(newText, attributes)
					: NumberValidator.isValidInteger(newText, attributes);

			attributes.setMinValue(savedMinValue);

			return valueIsValid;
		}
	};

	public VNumberField() {
		setStyleName(CSS_CLASSNAME);
		addKeyPressHandler(keyPressHandler);
	}

	@Override
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		super.updateFromUIDL(uidl, client);
		/*
		 * updateFromUIDL() is calling client.updateComponent() -> if this
		 * method returns, executing processAttributesFromServer() would be not
		 * needed, but we dunno if it returns or not. So we use hasAttribute()
		 * before getXAttribute() calls in the processAttributesFromServer()
		 * method to avoid setting invalid/undefined data.
		 */
		processAttributesFromServer(uidl);
	}

	private void processAttributesFromServer(UIDL uidl) {
		if (uidl.hasAttribute(ATTRIBUTE_ALLOW_NEGATIVES))
			attributes.setNegativeAllowed(uidl
					.getBooleanAttribute(ATTRIBUTE_ALLOW_NEGATIVES));

		if (uidl.hasAttribute(ATTRIBUTE_DECIMAL_PRECISION))
			attributes.setDecimalPrecision(uidl
					.getIntAttribute(ATTRIBUTE_DECIMAL_PRECISION));

		if (uidl.hasAttribute(ATTRIBUTE_MIN_VALUE))
			attributes
					.setMinValue(uidl.getDoubleAttribute(ATTRIBUTE_MIN_VALUE));

		if (uidl.hasAttribute(ATTRIBUTE_MAX_VALUE))
			attributes
					.setMaxValue(uidl.getDoubleAttribute(ATTRIBUTE_MAX_VALUE));

		if (uidl.hasAttribute(ATTRIBUTE_ALLOW_DECIMALS))
			attributes.setDecimalAllowed(uidl
					.getBooleanAttribute(ATTRIBUTE_ALLOW_DECIMALS));

		if (uidl.hasAttribute(ATTRIBUTE_DECIMAL_SEPARATOR))
			attributes.setDecimalSeparator((char) uidl
					.getIntAttribute(ATTRIBUTE_DECIMAL_SEPARATOR));

		if (uidl.hasAttribute(ATTRIBUTE_USE_GROUPING))
			attributes.setGroupingUsed(uidl
					.getBooleanAttribute(ATTRIBUTE_USE_GROUPING));

		if (uidl.hasAttribute(ATTRIBUTE_GROUPING_SEPARATOR))
			attributes.setGroupingSeparator((char) uidl
					.getIntAttribute(ATTRIBUTE_GROUPING_SEPARATOR));

		if (uidl.hasAttribute(ATTRIBUTE_SERVER_FORMATTED_VALUE))
			setValue(uidl.getStringAttribute(ATTRIBUTE_SERVER_FORMATTED_VALUE));
	}

	private boolean isControlKey(int keyCode) {
		switch (keyCode) {
		case KeyCodes.KEY_LEFT:
		case KeyCodes.KEY_RIGHT:
		case KeyCodes.KEY_UP:
		case KeyCodes.KEY_DOWN:
		case KeyCodes.KEY_BACKSPACE:
		case KeyCodes.KEY_DELETE:
		case KeyCodes.KEY_TAB:
		case KeyCodes.KEY_ENTER:
		case KeyCodes.KEY_ESCAPE:
		case KeyCodes.KEY_HOME:
		case KeyCodes.KEY_END:
			return true;
		}

		return false;
	}

	private String getFieldValueAsItWouldBeAfterKeyPress(char charCode) {
		int index = getCursorPos();
		String previousText = getText();

		if (getSelectionLength() > 0) {
			return previousText.substring(0, index)
					+ charCode
					+ previousText.substring(index + getSelectionLength(),
							previousText.length());
		} else {
			return previousText.substring(0, index) + charCode
					+ previousText.substring(index, previousText.length());
		}
	}

}
