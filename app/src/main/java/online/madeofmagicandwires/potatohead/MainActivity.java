package online.madeofmagicandwires.potatohead;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This already worked as is so I didn't change anything.
    }

    /**
     * Onclick function for checkboxes. Finds the corresponding ImageView and toggles its visibility.
     * Used to be checkClicked(); renamed to better descriptor
     *
     * @param v Checkbox object that called the function.
     */
    public void toggleBodyPart(View v ) {
        CheckBox box = (CheckBox) v;
        String boxText = box.getText().toString();

        try {
            View image = findViewByIdString(boxText);
            toggleVisibility(image);
            Log.d("potato",
                    "toggled visibility of " + image.getContentDescription().toString());
        } catch(Resources.NotFoundException e){
            Log.w("potato", e.getMessage());
        }

        // Log.d("potato", boxText);


    }

    /**
     * Searches Resources for a View with the id named String
     * example: findViewByIdString("foobar") returns R.id.foobar
     * @param  idString: a String of the id
     * @return the found View
     * @see 'https://stackoverflow.com/a/4865350' for inspiration
     * @throws Resources.NotFoundException;
     *
     */
    public View findViewByIdString(String idString){

        int resId = getResources().getIdentifier(idString, "id", getPackageName());
        if(resId != 0){
            return findViewById(resId);
        } else {
            throw new Resources.NotFoundException("No View by id " + idString + " found.");
        }

    }

    /**
     * toggles the visibility of a View between 0 and 1
     * @param v: a view object to toggle visibility on
     *
     **/
    public void toggleVisibility(View v) {
        if (v.getVisibility() == View.INVISIBLE) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.INVISIBLE);
        }
    }
}
