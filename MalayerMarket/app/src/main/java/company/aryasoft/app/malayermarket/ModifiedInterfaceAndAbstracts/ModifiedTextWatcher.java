package company.aryasoft.app.malayermarket.ModifiedInterfaceAndAbstracts;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class ModifiedTextWatcher implements TextWatcher
{
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    { }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    { }
    @Override
    public void afterTextChanged(Editable s)
    { }

}
