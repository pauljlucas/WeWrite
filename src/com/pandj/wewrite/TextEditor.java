package com.pandj.wewrite;

import java.util.Stack;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TextEditor extends Activity implements OnClickListener
{

  private TextView textBox;
  private Button undo, redo, disconnect;
  
  private Stack<panCake> localUndoStack;
  private Stack<panCake> localRedoStack;
  private Stack<panCake> remoteStack;
  private panCake redoUndoChecker;
  
  private String localText;
  private int cursorLocation;
  private ColabrifyClientObject client;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_text_editor);
    client = ColabrifyClientObject.getInstance();


    redoUndoChecker = null;
    //CollabrifyClient and Listener should be moved here.
    
    textBox = (TextView) findViewById(R.id.editText1);
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
    
    disableButton(undo);
    disableButton(redo);
    
    textBox.addTextChangedListener(new TextWatcher() 
    {  
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) 
      {
          // Don't think we have to do anything here
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) 
      {
        try{
          if(((redoUndoChecker != null)&&(redoUndoChecker != localUndoStack.peek())&&(redoUndoChecker != localRedoStack.peek())))
          {
            panCake toTheStack = new panCake();
            toTheStack.text = s.toString();
            toTheStack.index = cursorLocation + after;
            toTheStack.valid = false;//For right now
            localUndoStack.push(toTheStack);
            enableButton(undo);
          }
        }
        catch(Exception e)
        {
          //possibility of emptyStackExceptions
        }
      }

      @Override
      public void afterTextChanged(Editable s) 
      {
        localText = s.toString();
      }
  });
    
  }
  
  private class panCake
  {
    String text;
    long index;
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
          redoUndoChecker = obj;
          //Might need to change validity later
          localRedoStack.push(obj);
          enableButton(redo);
          if(localUndoStack.isEmpty())
          {
            disableButton(undo);
          }
          textBox.setText(obj.text);
        }
        break;
      case(R.id.redo) :
        if(!localRedoStack.isEmpty())
        {
          panCake obj = localRedoStack.pop();
          redoUndoChecker = obj;
          localUndoStack.push(obj);
          enableButton(undo);
          if(localRedoStack.isEmpty())
          {
            disableButton(redo);
          }
          textBox.setText(obj.text);
        }
        break;
      case(R.id.disconnect) :
        break;
    }
  }

}
