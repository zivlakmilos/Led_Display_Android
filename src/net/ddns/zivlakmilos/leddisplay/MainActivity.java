package net.ddns.zivlakmilos.leddisplay;

import android.support.v7.app.AppCompatActivity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
	
	public static int FadeAnimationDuration = 500;
	
	private LinearLayout m_layoutConnection;
	private LinearLayout m_layoutCommunication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		m_layoutConnection =
				(LinearLayout)findViewById(R.id.layoutConnection);
		m_layoutCommunication =
				(LinearLayout)findViewById(R.id.layoutCommunication);
		
		m_layoutCommunication.setVisibility(View.GONE);
		
		Button btnConnect = (Button)findViewById(R.id.btnConnect);
		btnConnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View sender) {
				showCommunication();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_exit) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showConnection() {
	}
	
	private void showCommunication() {
		m_layoutCommunication.setAlpha(0.0f);
		m_layoutConnection.setAlpha(1.0f);
		
		m_layoutConnection.animate()
						  .alpha(0.0f)
						  .setDuration(FadeAnimationDuration)
						  .setListener(new AnimatorListenerAdapter() {
							
							@Override
							public void onAnimationEnd(Animator animation) {
								m_layoutConnection.setVisibility(View.GONE);
								m_layoutCommunication.setVisibility(View.VISIBLE);
								m_layoutCommunication.animate()
													 .alpha(1.0f)
													 .setDuration(FadeAnimationDuration);
							}
						});
	}
}
