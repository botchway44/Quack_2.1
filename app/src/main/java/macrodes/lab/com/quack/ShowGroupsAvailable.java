package macrodes.lab.com.quack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;
@AutoSaveFields
public class ShowGroupsAvailable extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_groups_available);

        getSupportActionBar().hide();

        String myId = SimplePreferences.with(ShowGroupsAvailable.this).getSharedString("account","userId");

    }

    public void backButtonClicked(View view) {
        finish();
    }

public class ShowAllFriendRequest extends BaseAdapter{

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
}
