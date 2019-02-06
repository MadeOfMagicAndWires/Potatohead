package online.madeofmagicandwires.potatohead;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridLayout;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.Serializable;

@SuppressWarnings({"WeakerAccess", "JavaDoc"})
public class MainActivity extends AppCompatActivity {

    /**
     * Serializable SparseIntArray class. Required to send it to outState.
     *
     * @see java.io.Serializable
     * @see "https://stackoverflow.com/a/21574953" for inspiration.
     */
    @SuppressWarnings({"unused"})
    public static class SerializableSparseIntArray extends SparseIntArray implements Serializable{

        /** needed for custom serializations **/
        private static final long serialVersionUID = 5939429315080188394L;

        /**
         * Constructor specifying an initial size to allocate
         * @param initialCapacity the initial amount of capacity to pre-allocate memory for
         */
        public SerializableSparseIntArray(int initialCapacity){
            super(initialCapacity);
        }

        /**
         * Standard no-frills constructor
         */
        public SerializableSparseIntArray() {
            super();
        }

        /**
         * Writes object data to OutputStream
         */
        private void writeObject(ObjectOutputStream oos) throws IOException {
            Object[] data = new Object[size()];

            for(int i=0; i < size(); i++){
                Object[] pair = {keyAt(i), valueAt(i)};
                data[i] = pair;
            }
            oos.writeObject(data);
        }

        /**
         * Reads object data from input stream
         * @param ois the inputstream to read from
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            Object[] data = (Object[]) ois.readObject();
            for(Object aData : data) {
                Object[] pair = (Object[]) aData;
                this.append((int) pair[0], (int) pair[1]);
            }

        }

    }
    /** Used to save the toggled body parts' id and its visibility state **/
    private SerializableSparseIntArray visibilityState;


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

        if(savedInstanceState != null && savedInstanceState.containsKey("visibility")) {
            visibilityState = (SerializableSparseIntArray)
                    savedInstanceState.getSerializable("visibility");
            if(visibilityState != null) {
                Log.d("retrieved visibility states:", visibilityState.toString());
                restoreVisibilityState(visibilityState);
            }
        } else  {
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
        if(v.getId() != View.NO_ID) {
            state.put(v.getId(), v.getVisibility());
        }
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
     * Searches Resources for a View id by name
     * e.g: findViewByIdString("foobar") returns R.id.foobar
     * @param  idString: a id name of the View to return
     * @return the found View
     * @see 'https://stackoverflow.com/a/4865350' for inspiration
     * @throws Resources.NotFoundException;
     *
     */
    public View findViewByIdString(String idString) throws Resources.NotFoundException {

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
     * OnClick method for the Reset button
     * Resets all toggled views back to invisible
     * @param v view to be clicked, necessary for the onclick interface, but not used.
     */
    public void resetVisibility(View v){
        for(int i=0;i<visibilityState.size();i++) {
            findViewById(visibilityState.keyAt(i)).setVisibility(View.INVISIBLE);
            visibilityState.put(visibilityState.keyAt(i), View.INVISIBLE);
        }
        GridLayout grid = findViewById(R.id.GRID);
        for(int i=0;i<grid.getChildCount();i++){
            ((CheckBox)grid.getChildAt(i)).setChecked(false);
        }
    }



    /**
     * Onclick method for checkboxes. Finds the corresponding ImageView and toggles its visibility.
     * Used to be checkClicked(); renamed to better descriptor
     *
     * @param v Checkbox object that called the method.
     */
    public void toggleBodyPart(View v ) {
        CheckBox box = (CheckBox) v;
        String boxText = box.getTag().toString().toLowerCase();

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
