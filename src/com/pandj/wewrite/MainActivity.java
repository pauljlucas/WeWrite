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

public class MainActivity extends Activity implements OnClickListener
{
  private Button createUser, joinSession, createSession;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    //Good place for singleton injection
    
    
    //Get the buttons
    createUser = (Button) findViewById(R.id.createUser);
    joinSession = (Button) findViewById(R.id.joinSession);
    createSession = (Button) findViewById(R.id.createSession);
    
    //See if the preferences are set up
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    String email = preferences.getString("email","");
    String userName = preferences.getString("username","");
    
    if(TextUtils.isEmpty(email)||(TextUtils.isEmpty(userName)))
    {
      joinSession.setClickable(false);
      joinSession.setEnabled(false);
      createSession.setClickable(false);
      createSession.setEnabled(false);
      createUser.setText("Create User");

    }
    else
    {
      joinSession.setClickable(true);
      joinSession.setEnabled(true);
      createSession.setClickable(true);
      createSession.setEnabled(true);
      createUser.setText("Change User");
    }
    
    //Set the listeners 
    createUser.setOnClickListener(this);
    joinSession.setOnClickListener(this);
    createSession.setOnClickListener(this);
        
    setContentView(R.layout.activity_main);
  }

  @Override
  public void onBackPressed()
  {
    //Do nothing, avoiding the hairy situations that could arise
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public void onClick(View v)
  {
    switch(v.getId())
    {
      case(R.id.createSession) :
        break;
      
      case(R.id.createUser) :
        Intent getInfo = new Intent(this, GetEmailAndDisplayName.class);
        startActivity(getInfo);
        break;
        
      case(R.id.joinSession) :
        //Not the actual implementation, I just want to be able to get to the text box screen
        Intent textEdit = new Intent(this, TextEditor.class);
        startActivity(textEdit);
        break;
    }
  
    
  }

}
