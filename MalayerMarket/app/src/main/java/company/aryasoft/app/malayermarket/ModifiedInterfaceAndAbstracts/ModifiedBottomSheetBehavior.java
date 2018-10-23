package company.aryasoft.app.malayermarket.ModifiedInterfaceAndAbstracts;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

public abstract class ModifiedBottomSheetBehavior extends BottomSheetBehavior.BottomSheetCallback
{
    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset)
    { }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState)
    { }
}
