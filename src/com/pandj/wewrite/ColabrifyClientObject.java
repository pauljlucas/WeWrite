package com.pandj.wewrite;

import java.util.List;

import edu.umich.imlc.collabrify.client.CollabrifyClient;
import edu.umich.imlc.collabrify.client.CollabrifyListener;
import edu.umich.imlc.collabrify.client.CollabrifyParticipant;
import edu.umich.imlc.collabrify.client.CollabrifySession;
import edu.umich.imlc.collabrify.client.exceptions.CollabrifyException;

public class ColabrifyClientObject implements CollabrifyListener
{
  @Override
  public void onSessionCreated(long id)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public byte[] onBaseFileChunkRequested(long currentBaseFileSize)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void onBaseFileUploadComplete(long baseFileSize)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onSessionJoined(long maxOrderId, long baseFileSize)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onBaseFileChunkReceived(byte[] baseFileChunk)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onDisconnect()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onReceiveEvent(long orderId, int submissionRegistrationId,
      String eventType, byte[] data)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onReceiveSessionList(List<CollabrifySession> sessionList)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onParticipantJoined(CollabrifyParticipant p)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onParticipantLeft(CollabrifyParticipant p)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onError(CollabrifyException e)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onSessionEnd(long id)
  {
    // TODO Auto-generated method stub

  }

}
