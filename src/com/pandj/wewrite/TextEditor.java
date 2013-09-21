package com.pandj.wewrite;

import java.util.Stack;

import android.os.Bundle;
import android.app.Activity;
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
  private Spannable.Factory cursorObject;
  private ColabrifyClientObject client;
  
  private class customListener implements TextWatcher, OnClickListener
  {

    @Override
    public void onClick(View v)
    {
      // TODO: Cursor Change!
      
    }
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
      //String temp = new String(localText.substring(0, start) + s.subSequence(start, start + count)
      //    + localText.substring(secondCut, localText.length()));
      String temp = s.toString();

      localText = temp;
      panCake toTheStack = new panCake();
      toTheStack.text = localText;
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
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_text_editor);
    client = ColabrifyClientObject.getInstance();
    
    cursorObject = Spannable.Factory.getInstance();
    
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
    edgeCase.text = localText;
    edgeCase.valid = true;
    edgeCase.index = 0;
    
    localUndoStack.push(edgeCase);
    
    
    disableButton(undo);
    disableButton(redo);
    
    textBoxListener = new customListener();
    textBox.addTextChangedListener(textBoxListener);
    textBox.setOnClickListener(textBoxListener);
  }
  
  private class panCake
  {
    String text;
    long index;
    long length;
    boolean valid;
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
          textBox.setText(obj.text);
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
          textBox.setText(obj.text);
          textBox.addTextChangedListener(textBoxListener);
        }
        break;
      case(R.id.disconnect) :
        break;
    }
  }

}
