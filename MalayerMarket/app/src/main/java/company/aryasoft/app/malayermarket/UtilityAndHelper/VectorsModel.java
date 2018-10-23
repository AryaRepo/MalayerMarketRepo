package company.aryasoft.app.malayermarket.UtilityAndHelper;

import android.view.View;

/**
 * Created by Mac_Dev on 10/15/2017.
 */

public class VectorsModel
{
    public int draws;
    public View view ;
    public VectorConfig.MyDirType dirs;
    public VectorsModel(int drawable, View v, VectorConfig.MyDirType dir)
    {
        this.draws = drawable;
        this.view = v;
        this.dirs = dir;
    }

}
