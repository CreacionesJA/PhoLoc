package com.jad.pholoc.util;

import com.jad.pholoc.R;
import com.jad.pholoc.util.LocationsSQLite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

/**
 * Dialog to confirm / cancel removal of a location
 *
 * @author Jorge Alvarado
 */
public class DeleteLocationDialog extends DialogFragment {

    public final String TAG = this.getClass().getSimpleName();
    private int idLocation;

    /**
     * Empty Constructor
     */
    public DeleteLocationDialog() {
    }

    public void setIdLocation(int idLocation) {
        this.idLocation = idLocation;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.dialog_deletelocation_message)
                .setTitle(R.string.dialog_confirm)
                .setPositiveButton(R.string.dialog_accept,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                LocationsSQLite dbloc = new LocationsSQLite(getActivity(), "DBLocations.db",
                                        null, 1);
                                // Remove location
                                dbloc.deleteLocation(idLocation);
                                // Close the activity
                                getActivity().finish();
                                Log.i("Dialogos", "Confirmacion Aceptada.");
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(R.string.dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.i("Dialogos", "Confirmacion Cancelada.");
                                dialog.cancel();
                            }
                        });

        return builder.create();
    }
}