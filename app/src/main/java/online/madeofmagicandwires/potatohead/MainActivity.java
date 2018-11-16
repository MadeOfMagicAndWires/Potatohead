package online.madeofmagicandwires.potatohead;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.CheckBox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.Serializable;


public class MainActivity extends AppCompatActivity {

    /**
     * Serializable SparseIntArray class. Required to send it to outState.
     *
     * @see java.io.Serializable
     * @see "https://stackoverflow.com/a/21574953" for inspiration.
     */
    public class SerializableSparseIntArray extends SparseIntArray implements Serializable{

        private static final long serialVersionUID = 5939429315080188394L;

        public SerializableSparseIntArray(int initialCapacity){
            super(initialCapacity);
        }

        public SerializableSparseIntArray() {
            super();
        }

        /**
         * Serializable required methods
         */
        private void writeObject(ObjectOutputStream oos) throws IOException {
            Object[] data = new Object[size()];

            for(int i=0; i < size(); i++){
                Object[] pair = {keyAt(i), valueAt(i)};
                data[i] = pair;
            }
            oos.writeObject(data);
        }

        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            Object[] data = (Object[]) ois.readObject();
            for(int i=0;i<data.length;i++) {
                Object[] pair = (Object[]) data[i];
                this.append((int) pair[0], (int) pair[1]);
            }

        }

    }
    public SerializableSparseIntArray visibilityState;

    /**
     * Restore visibility state of previously toggled ImageViews.
     * @param savedInstanceState Bundle of persistent data to restore.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Restore visibility state.
        // On initial run this doesn't exist, so create an empty SparseIntArray instead.

        try {
            visibilityState = (SerializableSparseIntArray)
                    savedInstanceState.getSerializable("visibility");
            Log.d("potato onCreate", visibilityState.toString());
            restoreVisibilityState(visibilityState);
        } catch(NullPointerException e) {
            Log.d("potato OnCreate", "savedInstance was null, creating new visibilityState");
            visibilityState = new SerializableSparseIntArray();
        }

    }

    /**
     * Stores reference and visibility of a view for later use.
     * @param v     View to store visibility of
     * @param state SparseIntArray to store reference in
     */
    public void storeVisibilityState(View v, SparseIntArray state){
        state.put(v.getId(), v.getVisibility());
    }

    /**
     * Restores visibility state of a view from a reference.
     * @param state database of stored visibility states.
     */
    public void restoreVisibilityState(SparseIntArray state){
        for(int i = 0;i < state.size(); i++){
            findViewById(state.keyAt(i)).setVisibility(state.valueAt(i));
        }
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
            // find corresponding imageView then toggle its visibility.
            View image = findViewByIdString(boxText);
            toggleVisibility(image);
            Log.d("potato",
                    "toggled visibility of " + image.getContentDescription().toString());

            // Store the new visibility into visibilityState.
            storeVisibilityState(image, visibilityState);
        } catch(Resources.NotFoundException e){
            Log.w("potato", e.getMessage());
        }

        // Log.d("potato", boxText);


    }

    /**
     * Saves visibilityState of toggled imageViews to recover onCreate.
     * @param outState Bundle of persistent data to restore at a later time.
     */
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        // Save visibility state
        outState.putSerializable("visibility", visibilityState);
        Log.d("potato onSaveInstance", "visibilityState saved");

    }
}
