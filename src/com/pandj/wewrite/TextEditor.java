package com.pandj.wewrite;

import java.util.Stack;

import edu.umich.imlc.collabrify.client.CollabrifyClient;
import edu.umich.imlc.collabrify.client.CollabrifyListener;
import edu.umich.imlc.collabrify.client.exceptions.CollabrifyException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TextEditor extends Activity implements OnClickListener
{

  private EditText textBox;
  private Button undo, redo, disconnect;
  
  private Stack<panCake> localUndoStack;
  private Stack<panCake> localRedoStack;
  private Stack<panCake> remoteStack;
  
  private String localText;
  private int cursorLocation;
  
  private String userName;
  private String email;
  private ColabrifyClientObject client;
  private CollabrifyClient myClient;
  
  private class customListener implements TextWatcher
  {

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) 
    {
      //This is the correct one to use. As long as we push the first string to the stack.
      //s is the entirety of the string that is going to show up after this key press
      int secondCut;
      if(before < count)//Insertion
      {
        secondCut = start + before;
      }
      else if( before == count)//Might caused issues with swap but fixes the triple stack problem
      {
        return;
      }
      else//Deletion
      {
        secondCut = localText.length();
      }
      String temp = s.toString();

      cursorLocation = start + count;
      localText = temp;
      panCake toTheStack = new panCake();
      toTheStack.textAfter = localText;
      toTheStack.cursorLocationAfter = cursorLocation;
      toTheStack.valid = false;//For right now
      localUndoStack.push(toTheStack);
      enableButton(undo);
    }
    

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) 
    {

    }

    @Override
    public void afterTextChanged(Editable s) 
    {

    }

 
  }
  private customListener textBoxListener;
  
  @Override
  protected void onResume()
  {
    super.onResume();
    
  }
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_text_editor);

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    email = preferences.getString("email","NOTSET");
    userName = preferences.getString("username","NOTSET");
    
    textBoxListener = new customListener();
    textBox.addTextChangedListener(textBoxListener);
    
    try
    {
      myClient = new CollabrifyClient(this, email, userName, "411fall2013@umich.edu", "XY3721425NoScOpE", true, (CollabrifyListener) textBoxListener);
    }
    catch( CollabrifyException e )
    {
      e.printStackTrace();
    }

    textBox = (EditText) findViewById(R.id.editText1);
    undo = (Button) findViewById(R.id.undo);
    redo = (Button) findViewById(R.id.redo);
    disconnect = (Button) findViewById(R.id.disconnect);
    
    undo.setOnClickListener(this);
    redo.setOnClickListener(this);
    disconnect.setOnClickListener(this);
    
    localUndoStack = new Stack<panCake>();
    localRedoStack = new Stack<panCake>();

    localText = "";//This will change if we are joining an already existing session
    cursorLocation = 0;

    panCake edgeCase = new panCake();
    edgeCase.textAfter = localText;
    edgeCase.valid = true;
    
    localUndoStack.push(edgeCase);
    
    
    disableButton(undo);
    disableButton(redo);
    

  }
  
  private class panCake implements Runnable 
  {
    
    void panCake(ColabrifyClientObject c)
    {
      
    }
    //These could all be encapsulated in a proto buffer class
    String textAfter;
    String textBefore;
    int cursorLocationAfter;
    int cursorLocationBefore;
    long globalOrderId;
    boolean valid;
    
    public void changeText(EditText t)
    {
      
    }

    @Override
    public void run()
    {
      // TODO Auto-generated method stub
      
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
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.text_editor, menu);
    return true;
  }

  @Override
  public void onClick(View v)
  {
    switch(v.getId())
    {
      case(R.id.undo) :
        if(!localUndoStack.isEmpty())
        {
          panCake obj = localUndoStack.pop();
          //Might need to change validity later
          localRedoStack.push(obj);
          enableButton(redo);
          if(localUndoStack.isEmpty())
          {
            disableButton(undo);
          }
          textBox.removeTextChangedListener(textBoxListener);
          textBox.setText(obj.textAfter);
          textBox.setSelection(obj.cursorLocationAfter);
          textBox.addTextChangedListener(textBoxListener);
        }
        break;
      case(R.id.redo) :
        if(!localRedoStack.isEmpty())
        {
          panCake obj = localRedoStack.pop();
          localUndoStack.push(obj);
          enableButton(undo);
          if(localRedoStack.isEmpty())
          {
            disableButton(redo);
          }
          textBox.removeTextChangedListener(textBoxListener);
          textBox.setText(obj.textAfter);
          textBox.setSelection(obj.cursorLocationAfter);
          textBox.addTextChangedListener(textBoxListener);
        }
        break;
      case(R.id.disconnect) :
        break;
    }
  }

}
