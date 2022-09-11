package com.compi.dinhnt.travelplanner

import android.widget.EditText
import androidx.core.widget.doAfterTextChanged


fun EditText.validate(validator: (String) -> Boolean, messageError: String) {
    this.doAfterTextChanged {
        this.error = if (validator(it.toString())) null else messageError
    }
}
