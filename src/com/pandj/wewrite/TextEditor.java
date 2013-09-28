package com.pandj.wewrite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pandj.wewrite.javaProtoOutput;
import com.pandj.wewrite.javaProtoOutput.protoData;

import edu.umich.imlc.collabrify.client.CollabrifyClient;
import edu.umich.imlc.collabrify.client.CollabrifyListener;
import edu.umich.imlc.collabrify.client.CollabrifyParticipant;
import edu.umich.imlc.collabrify.client.CollabrifySession;
import edu.umich.imlc.collabrify.client.exceptions.CollabrifyException;
import edu.umich.imlc.collabrify.client.exceptions.LeaveException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
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

public class TextEditor extends Activity implements OnClickListener, CollabrifyListener
{

  private EditTextSelection textBox;
  private Button undo, redo, disconnect;
  
  private Stack<panCakeLocal> localUndoStack;
  private Stack<panCakeLocal> localRedoStack;
  private Stack<panCake> remoteStack;
  
  private String localText;
  private int cursorLocation;
  
  private boolean createNewSession;
  
  private String userName;
  private String email;
  private ColabrifyClientObject clientListener;
  private String sessionName; 
  private long sessionId;
  private long orderId;
  
  //CollabrifyListener Functions
  @Override
  public void onSessionCreated(long id)
  {
    sessionId = id;
    Log.i("CCO", "Session created.");
  }
  @Override
  public void onSessionJoined(long maxOrderId, long baseFileSize)
  {
    Log.i("CCO", "SessionJoinedCalled");
    if(baseFileSize != 0)
    {
    	Log.i("CCO", "Joined session with basefile!");
    	finish();
    }
    orderId = maxOrderId;
  }
  @Override
  public void onReceiveSessionList(final List<CollabrifySession> sessionList)
  {
    if( sessionList.isEmpty())
    {
    	Log.i("CCO", "No Session Available using Tags: " + clientListener.tags.get(0));
    	runOnUiThread(new Runnable()
    	{
    		@Override
    		public void run()
    		{
    	    	Toast.makeText(getBaseContext(), "No possible Sessions to Join", Toast.LENGTH_SHORT).show();
    	        finish();
    		}
    	});
    	return;
    }
    List<String> sessionNames = new ArrayList<String>();
    for(CollabrifySession s : sessionList)
    {
    	sessionNames.add(s.name());
    }
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Choose a session").setItems(
    		sessionNames.toArray(new String[sessionList.size()]), 
    		new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try
					{
						sessionId = sessionList.get(which).id();
						sessionName = sessionList.get(which).name();
						clientListener.myClient.joinSession(sessionId, null);
					}
					catch( CollabrifyException e)
					{
						Log.i("CCO", "Join Session Failed", e);
						finish();
					}
				}
			});
    runOnUiThread(new Runnable()
    {
		@Override
		public void run() {
				try{
				  builder.show();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}
    });
  }
  @Override
  public void onReceiveEvent(long orderId, int submissionRegistrationId,
      String eventType, byte[] data)
  {
	  Log.i("CCO", "Event recieved");
  }

  @Override
  public void onDisconnect()
  {
    Log.i("CCO", "Disconnect Triggered in Listener");

  }
  @Override 
  public void onError(CollabrifyException e)
  {
	  e.printStackTrace();
  }
  
  
  
  
  
  private class customListener implements TextWatcher 
  {

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) 
    {
      String temp = s.toString();

      panCakeLocal toTheStack = new panCakeLocal();
      StateInfo insert = new StateInfo();
      insert.textBefore = localText;
      insert.cursorLocationBefore = textBox.getcursorLocation();
      
      cursorLocation = start + count;
      localText = temp;
      
      insert.textAfter = localText;
      insert.cursorLocationAfter = cursorLocation;
      insert.valid = false;;//For right now
      insert.populateDifference();
      toTheStack.InsertLocalData(insert);
      
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
    this.setTitle(clientListener.sessionName);
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
    
	clientListener = new ColabrifyClientObject(getBaseContext(), createNewSession, email, userName, this);
	clientListener.enterSession();
      

    textBox = (EditTextSelection) findViewById(R.id.editText1);
    undo = (Button) findViewById(R.id.undo);
    redo = (Button) findViewById(R.id.redo);
    disconnect = (Button) findViewById(R.id.disconnect);
    
    undo.setOnClickListener(this);
    redo.setOnClickListener(this);
    disconnect.setOnClickListener(this);
    
    textBoxListener = new customListener();
    textBox.addTextChangedListener(textBoxListener);
    
    localUndoStack = new Stack<panCakeLocal>();
    localRedoStack = new Stack<panCakeLocal>();

    if(createNewSession)
    {
      localText = "";
      cursorLocation = 0;	
    }
    //Need to get the server state.
    localText = "";
    cursorLocation = 0;

    panCakeLocal edgeCase = new panCakeLocal();
    StateInfo s = new StateInfo();
    s.textAfter = localText;
    s.valid = true;
    edgeCase.InsertLocalData(s);
    localUndoStack.push(edgeCase);
    
    
    disableButton(undo);
    disableButton(redo);
    

  }

  private class StateInfo
  {
		public String textAfter = "";
		public String textBefore = "";
		public String differText = ""; 
		public int cursorLocationAfter = 0 ;
		public int cursorLocationBefore = 0;
		public long globalOrderId = 0;
		public boolean valid = false;
	    public void populateDifference() 
	    {
	      if(textAfter.length() == textBefore.length())
	      {
	    	  differText = "";
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
  }
  
  public class panCake 
  {
    protected protoData.Builder protoBuff = protoData.newBuilder();
    protected protoData data = null;
    protected StateInfo state;
    
  }
  
  private class panCakeLocal extends panCake implements Runnable 
  {
	    public void InsertLocalData(StateInfo insert)
	    {
	    	state = insert;
	    	protoBuff.setTextAfter(insert.textAfter);
	    	protoBuff.setTextBefore(insert.textBefore);
	    	protoBuff.setValid(insert.valid);
	    	protoBuff.setGlobalOrderId(insert.globalOrderId);
	    	protoBuff.setCursorLocationBefore(insert.cursorLocationBefore);
	    	protoBuff.setCursorLocationAfter(insert.cursorLocationAfter);
	    	protoBuff.setDifferText(insert.differText);
	    	
	    	data = protoBuff.build();
	    	byte[] test = data.toByteArray();
	    	try {
				protoData check = protoData.parseFrom(test);
				if(check == data)
				{
					Toast.makeText(getApplicationContext(), "It worked!", Toast.LENGTH_SHORT).show();
				}
			} catch (InvalidProtocolBufferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }
	    
	    @Override
	    public void run()
	    {
	      //Send it over the wire!
	      textBox.removeTextChangedListener(textBoxListener);
	      textBox.setText(this.state.textAfter);
	      textBox.setSelection(this.state.cursorLocationAfter);
	      textBox.addTextChangedListener(textBoxListener);
	    }
  }
  
  public class panCakeRemote extends panCake implements Runnable 
  {
    public panCakeRemote(byte[] input) 
    {
    	try 
    	{
			data = protoData.parseFrom(input);
			state.cursorLocationAfter = data.getCursorLocationAfter();
			state.cursorLocationBefore = data.getCursorLocationBefore();
			state.valid = data.getValid();
			state.globalOrderId = data.getGlobalOrderId();
			state.textAfter = data.getTextAfter();
			state.textBefore = data.getTextBefore();
			state.differText = data.getDifferText();
		} catch (InvalidProtocolBufferException e) 
		{
			e.printStackTrace();
		}
    	this.run();
	}
    
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		
	}
    //send and receive functions for both
    //Also make it so that when something is added it adds it to both protocol buffers and local vars
    //LOOK FOR BYTE ARRAY IN HOWTO Set up collabrify
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
	  clientListener.destroy();
  }
  @Override
  public void onClick(View v)
  {
    switch(v.getId())
    {
      case(R.id.undo) :
        if(!localUndoStack.isEmpty())
        {
          panCakeLocal obj = localUndoStack.pop();
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
          panCakeLocal obj = localRedoStack.pop();
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
    	clientListener.destroy();
      	this.finish();
        break;
    }
  }
@Override
public byte[] onBaseFileChunkRequested(long currentBaseFileSize) {
	// TODO Auto-generated method stub
	return null;
}
@Override
public void onBaseFileUploadComplete(long baseFileSize) {
	// TODO Auto-generated method stub
	
}
@Override
public void onBaseFileChunkReceived(byte[] baseFileChunk) {
	// TODO Auto-generated method stub
	
}
@Override
public void onParticipantJoined(CollabrifyParticipant p) {
	// TODO Auto-generated method stub
	
}
@Override
public void onParticipantLeft(CollabrifyParticipant p) {
	// TODO Auto-generated method stub
	
}
@Override
public void onSessionEnd(long id) {
	// TODO Auto-generated method stub
	
}
  

}
