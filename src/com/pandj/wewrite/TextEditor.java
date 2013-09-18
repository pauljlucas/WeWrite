package com.pandj.wewrite;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

public class TextEditor extends Activity
{

  private TextView textBox;
  private Button undo, redo;
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_text_editor);

    textBox = (TextView) findViewById(R.id.editText1);
    undo = (Button) findViewById(R.id.undo);
    redo = (Button) findViewById(R.id.redo);
    
    setContentView(R.layout.activity_text_editor);
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
    getMenuInflater().inflate(R.menu.text_editor, menu);
    return true;
  }

}
