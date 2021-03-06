package com.pandj.wewrite;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener
{
  private Button createUser, joinSession, createSession;
  private TextView signedInStatus;
  private ConnectionDetector internetStatus;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    internetStatus = new ConnectionDetector(this.getBaseContext());
    
    //Get the buttons
    createUser = (Button) findViewById(R.id.createUser);
    joinSession = (Button) findViewById(R.id.joinSession);
    createSession = (Button) findViewById(R.id.createSession);
    signedInStatus = (TextView) findViewById(R.id.userStatus);
    
     
    //Set the listeners 
    createUser.setOnClickListener(this);
    joinSession.setOnClickListener(this);
    createSession.setOnClickListener(this);
    
       
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    //See if the preferences are set up
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    String email = preferences.getString("email","NOTSET");
    String userName = preferences.getString("username","NOTSET");
    
    if(TextUtils.equals("NOTSET", email)||(TextUtils.equals("NOTSET", userName)))
    {
      disableButton(joinSession);
      disableButton(createSession);
      createUser.setText("Create User");
      signedInStatus.setText("Not Signed In");
    }
    else if(internetStatus.isConnectingToInternet())
    {
      enableButton(joinSession);
      enableButton(createSession);
      enableButton(createUser);
      createUser.setText("Change User");
      signedInStatus.setText("Signed in as: " + userName + ", " + email);
    }
    else
    {
      disableButton(joinSession);
      disableButton(createSession);
      disableButton(createUser);
      signedInStatus.setText("Please connect to the internet and reload.");
    }
  }
  
  private void disableButton(Button b)
  {
    b.setClickable(false);
    b.setEnabled(false);
    b.setVisibility(View.GONE);
  }
  
  private void enableButton(Button b)
  {
    b.setClickable(true);
    b.setEnabled(true);
    b.setVisibility(View.VISIBLE);
  }
  
  @Override
  public void onBackPressed()
  {
    //Do nothing, avoiding the hairy situations that could arise
  }

  @Override
  public void onClick(View v)
  {
	Intent nextScreen = null;
    switch(v.getId())
    {
      case(R.id.createSession) :
          nextScreen = new Intent(this, TextEditor.class);
       	  nextScreen.putExtra("Create", true);
          startActivity(nextScreen);
        break;
      
      case(R.id.createUser) :
          nextScreen = new Intent(this, GetEmailAndDisplayName.class);
          startActivity(nextScreen);
        break;
        
      case(R.id.joinSession) :
	      nextScreen = new Intent(this, TextEditor.class);
	   	  nextScreen.putExtra("Create", false);
	      startActivity(nextScreen);
        break;
    }
  
    
  }

}
