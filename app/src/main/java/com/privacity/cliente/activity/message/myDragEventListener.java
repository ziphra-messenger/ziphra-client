package com.privacity.cliente.activity.message;

import android.content.ClipData;
import android.content.ClipDescription;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageButton;

public class myDragEventListener implements View.OnDragListener {
    ImageButton messageDeleteAudio;
    public myDragEventListener(ImageButton messageDeleteAudio) {
        this.messageDeleteAudio = messageDeleteAudio;
    }

    // This is the method that the system calls when it dispatches a drag event to the
    // listener.
    public boolean onDrag(View v, DragEvent event) {

        // Defines a variable to store the action type for the incoming event
        final int action = event.getAction();

        // Handles each of the expected events
        switch(action) {

            case DragEvent.ACTION_DRAG_STARTED:

                // Determines if this View can accept the dragged data
                return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                // Returns false. During the current drag and drop operation, this View will
                // not receive events again until ACTION_DRAG_ENDED is sent.

            case DragEvent.ACTION_DRAG_ENTERED:


                return true;

            case DragEvent.ACTION_DRAG_LOCATION:

                // Ignore the event
                return true;

            case DragEvent.ACTION_DRAG_EXITED:


                return true;

            case DragEvent.ACTION_DROP:

                // Gets the item containing the dragged data
                ClipData.Item item = event.getClipData().getItemAt(0);

                // Gets the text data from the item.
                CharSequence dragData = item.getText();

                return true;

            case DragEvent.ACTION_DRAG_ENDED:




                if (event.getY() > messageDeleteAudio.getY() - 100 && event.getY() < messageDeleteAudio.getY() + 3000){
                    if (event.getX() > messageDeleteAudio.getX() - 100 && event.getX() < messageDeleteAudio.getX() + 300){

                        messageDeleteAudio.setVisibility(View.INVISIBLE);
                    }

                }

            return true;

            // An unknown action type was received.
            default:
                //Log.e("DragDrop Example","Unknown action type received by OnDragListener.");
                break;
        }

        return false;
    }
}