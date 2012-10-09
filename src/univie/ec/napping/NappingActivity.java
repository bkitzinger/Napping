package univie.ec.napping;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class NappingActivity extends Activity implements AdapterView.OnItemClickListener{
   
	String[] items;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
      //Configure
        try {
			Configuration.initialize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Initialize number of videos
        int videocount = Configuration.videos.length;
        items = new String[videocount];
        for(int i=0; i<videocount; i++){
        	items[i] = "| "+String.valueOf(i+1)+" |";
        }
        
        
        

		GridView gv = (GridView) findViewById(R.id.grid);

		ArrayAdapter<String> aa = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_list_item_1, 
				items );

		gv.setAdapter(aa);
		gv.setOnItemClickListener((OnItemClickListener) this);
		
		
		
    }
    
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    	//Toast.makeText(NappingActivity.this, "" + position + id, Toast.LENGTH_SHORT).show();
    	Intent showvideo = new Intent(this, ViewActivity.class);
    	showvideo.putExtra("videoID", position);
    	startActivity(showvideo);
	}
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.playall:     
            	Intent showvideo = new Intent(this, PlayAllActivity.class);
            	startActivity(showvideo);
                                break;
            case R.id.prefs:     
            	Intent showpreferences = new Intent(this, Preferences.class);
            	startActivity(showpreferences);
                                break;
                                
            case R.id.about:
            	final Dialog dialog = new Dialog(NappingActivity.this);
                dialog.setContentView(R.layout.aboutdialog);
                dialog.setTitle("About/Manual");
                dialog.setCancelable(true);
                //there are a lot of settings, for dialog, check them all out!
 
                //set up text
                TextView text = (TextView) dialog.findViewById(R.id.AboutText);
                text.setText(R.string.about_text);
 
                //set up button
                Button button = (Button) dialog.findViewById(R.id.CancelBtn);
                button.setOnClickListener(new OnClickListener() {
                @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                //now that the dialog is set up, it's time to show it    
                dialog.show();
        }
        return true;
    }
}