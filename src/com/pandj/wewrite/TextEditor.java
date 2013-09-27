package com.pandj.wewrite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import com.pandj.wewrite.javaProtoOutput;
import com.pandj.wewrite.javaProtoOutput.protoData;

import edu.umich.imlc.collabrify.client.CollabrifyClient;
import edu.umich.imlc.collabrify.client.CollabrifyListener;
import edu.umich.imlc.collabrify.client.exceptions.CollabrifyException;
import edu.umich.imlc.collabrify.client.exceptions.LeaveException;
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
  
  private boolean createNewSession;
  
  private String userName;
  private String email;
  private ColabrifyClientObject clientListener;
  private CollabrifyClient myClient;
  private String sessionName;
  
  private final List<String> tags = Arrays.asList("jbarno", "lucaspa");
 
 
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
      toTheStack.EditTextBefore(localText);
      toTheStack.EditcursorLocationBefore(textBox.getcursorLocation());
      cursorLocation = start + count;
      localText = temp;
      toTheStack.EditTextAfter(localText);
      toTheStack.EditcursorLocationAfter(cursorLocation);
      toTheStack.EditValid(false);//For right now
      
      //How to convert. This may or may not work...DELETE THIS
      byte[] bptest = null;
      protoData.Builder test = null; 
      try
      {
        bptest = BytePrep.toBytes(toTheStack.protoBuff);
      }
      catch( IOException e1 )
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      try
      {
        test =   (protoData.Builder) BytePrep.fromBytes(bptest);
      }
      catch( IOException e1 )
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      catch( ClassNotFoundException e1 )
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      
      //END DELETE THIS
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
    
    Bundle extras = getIntent().getExtras();
    createNewSession = extras.getBoolean("Create");
    try
    {
      clientListener = new ColabrifyClientObject(this.getBaseContext(), createNewSession);
      //myClient = new CollabrifyClient(this, email, userName, "411fall2013@umich.edu", "XY3721425NoScOpE", true, clientListener);
      if(createNewSession)
      {
    	  Random rand = new Random();
    	  sessionName = "Test" + rand.nextInt();
    	  myClient.createSession(sessionName, tags, null, 10);
    	  this.setTitle(sessionName);
      }
      else
      {
    	  myClient.requestSessionList(tags);
      }
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
    edgeCase.EditTextAfter(localText);
    edgeCase.EditValid(true);

    
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
    protoData.Builder protoBuff = protoData.newBuilder();
    
    public void EditTextAfter(String a){
      textAfter = a;
      protoBuff.setTextAfter(a);
    }
    public void EditTextBefore(String b){
      textBefore = b;
      protoBuff.setTextBefore(b);
    }
    public void EditDifferText(String c){
      differText = c;
      protoBuff.setDifferText(c);
    }
    public void EditcursorLocationAfter(int d){
      cursorLocationAfter = d;
      protoBuff.setCursorLocationAfter(d);
    }
    public void EditcursorLocationBefore(int e){
      cursorLocationBefore = e;
      protoBuff.setCursorLocationBefore(e);
    }
    public void EditGlobalOrderId(long f){
      globalOrderId = f;
      protoBuff.setGlobalOrderId(f);
    }
    public void EditValid(boolean g){
      valid = g;
      protoBuff.setValid(g);
    }


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
  
  private class panCakeLocal extends panCake 
  {

  }
  
  private class panCakeRemote extends panCake 
  {
    //send and receive functions for both
    //Also make it so that when something is added it adds it to both protocol buffers and local vars
    //LOOK FOR BYTE ARRAY IN HOWTO Set up collabrify
    
  }
  
  public static class BytePrep {
    public static byte[] toBytes(Object obj) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ObjectOutputStream oOut = new ObjectOutputStream(bOut);
        oOut.writeObject(obj);
        return bOut.toByteArray();
    }

    public static Object fromBytes(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bIn = new ByteArrayInputStream(data);
        ObjectInputStream oIn = new ObjectInputStream(bIn);
        return oIn.readObject();
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
  protected void onDestroy()
  {
	  super.onDestroy();    	
	  if(myClient.inSession())
  	  {
  		try 
  		{
  			//If we are the creator and leaving, we want the session destroyed
  			myClient.leaveSession(createNewSession);
		} 
  		catch (LeaveException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CollabrifyException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	}
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
    	if(myClient.inSession())
    	{
    		try 
    		{
				myClient.leaveSession(false);
				this.finish();
			} catch (LeaveException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CollabrifyException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        break;
    }
  }

}
