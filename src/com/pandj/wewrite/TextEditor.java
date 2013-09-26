package com.pandj.wewrite;

import java.util.Stack;

import edu.umich.imlc.collabrify.client.CollabrifyClient;
import edu.umich.imlc.collabrify.client.CollabrifyListener;
import edu.umich.imlc.collabrify.client.exceptions.CollabrifyException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

class EditTextSelection extends EditText
{
  private int cursorLocation = 0;
  public EditTextSelection(Context context, AttributeSet attrs,
          int defStyle) {
      super(context, attrs, defStyle);

  }

  public EditTextSelection(Context context, AttributeSet attrs) 
  {
      super(context, attrs);
  }

  public EditTextSelection(Context context) 
  {
      super(context);
  }
  public int getcursorLocation()
  {
    return cursorLocation;
  }
  @Override   
  protected void onSelectionChanged(int selStart, int selEnd) { 
     cursorLocation = selEnd;
  } 
}
public class TextEditor extends Activity implements OnClickListener
{

  private EditTextSelection textBox;
  private Button undo, redo, disconnect;
  
  private Stack<panCake> localUndoStack;
  private Stack<panCake> localRedoStack;
  private Stack<panCake> remoteStack;
  
  private String localText;
  private int cursorLocation;
  
  private String userName;
  private String email;
  private ColabrifyClientObject clientListener;
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

      panCake toTheStack = new panCake();
      toTheStack.textBefore = localText;
      toTheStack.cursorLocationBefore = textBox.getcursorLocation();
      cursorLocation = start + count;
      localText = temp;
      toTheStack.textAfter = localText;
      toTheStack.cursorLocationAfter = cursorLocation;
      toTheStack.valid = false;//For right now
      try
      {
        toTheStack.populateDifference();
      }
      catch( Exception e )
      {
        e.printStackTrace();
      }
      //Toast.makeText(getBaseContext(), "Before: " + toTheStack.cursorLocationBefore + " After: " + toTheStack.cursorLocationAfter, Toast.LENGTH_SHORT).show();
      //Toast.makeText(getBaseContext(), "Diff|" + toTheStack.differText + "|", Toast.LENGTH_SHORT).show();

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
    
    try
    {
      clientListener = new ColabrifyClientObject();
      myClient = new CollabrifyClient(this, email, userName, "411fall2013@umich.edu", "XY3721425NoScOpE", true, clientListener);
    }
    catch( CollabrifyException e )
    {
      e.printStackTrace();
    }

    textBox = (EditTextSelection) findViewById(R.id.editText1);
    undo = (Button) findViewById(R.id.undo);
    redo = (Button) findViewById(R.id.redo);
    disconnect = (Button) findViewById(R.id.disconnect);
    
    undo.setOnClickListener(this);
    redo.setOnClickListener(this);
    disconnect.setOnClickListener(this);
    
    textBoxListener = new customListener();
    textBox.addTextChangedListener(textBoxListener);
    
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
    String differText; 
    int cursorLocationAfter;
    int cursorLocationBefore;
    long globalOrderId;
    boolean valid;

    public void changeText(EditText t)
    {
      
    }
    
    public void populateDifference() throws Exception
    {
      if(textAfter.length() == textBefore.length())
      {
        Exception e = new Exception();
        throw e;
      }
      if(textAfter.length() > textBefore.length())//Insertion
      {
        differText = textAfter.substring(cursorLocationBefore, cursorLocationAfter);
      }
      else
      {
        differText = textBefore.substring(cursorLocationAfter, cursorLocationBefore);
      }
    }

    @Override
    public void run()
    {
      textBox.removeTextChangedListener(textBoxListener);
      textBox.setText(this.textAfter);
      textBox.setSelection(this.cursorLocationAfter);
      textBox.addTextChangedListener(textBoxListener);
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
          obj.run();
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
          obj.run();
        }
        break;
      case(R.id.disconnect) :
        break;
    }
  }

}
