package myfilemanager.jiran.com.myfilemanager.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import myfilemanager.jiran.com.myfilemanager.R;
import myfilemanager.jiran.com.myfilemanager.activity.MainActivity;

public final class PasteTask extends AsyncTask<String, Void, List<String>> {

    private final WeakReference<Activity> activity;

    private ProgressDialog dialog;

    private final File location;

    private boolean success = false;

    public PasteTask(final Activity activity, File currentDir) {
        this.activity = new WeakReference<>(activity);
        this.location = currentDir;
    }

    @Override
    protected void onPreExecute() {
        final Activity activity = this.activity.get();

        if (activity != null) {
            this.dialog = new ProgressDialog(activity);

            if (ClipBoard.isMove())
                this.dialog.setMessage(activity.getString(R.string.moving));
            else
                this.dialog.setMessage(activity.getString(R.string.copying));

            this.dialog.setCancelable(true);
            this.dialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            cancel(false);
                        }
                    });
            if (!activity.isFinishing()) {
                this.dialog.show();
            }
        }
    }

    @Override
    protected List<String> doInBackground(String... content) {
        final List<String> failed = new ArrayList<>();
        final Activity activity = this.activity.get();
        ClipBoard.lock();

        for (String s : content) {
            String fileName = s.substring(s.lastIndexOf("/"), s.length());
            if (ClipBoard.isMove()) {
                Utils.moveToDirectory(new File(s), new File(location, fileName), activity);
                success = true;
            } else {
                Utils.copyFile(new File(s), new File(location, fileName), activity);
                success = true;
            }
        }

        if (location.canRead()) {
            for (File file : location.listFiles())
                MediaStoreUtils.addFileToMediaStore(file.getPath(), activity);
        }
        return failed;
    }

    @Override
    protected void onPostExecute(final List<String> failed) {
        super.onPostExecute(failed);
        this.finish(failed);
    }

    @Override
    protected void onCancelled(final List<String> failed) {
        super.onCancelled(failed);
        this.finish(failed);
    }

    private void finish(final List<String> failed) {
        if (this.dialog != null) {
            this.dialog.dismiss();
        }

        final Activity activity = this.activity.get();

        if (ClipBoard.isMove()) {
            if (success)
                Toast.makeText(activity,
                        activity.getString(R.string.movesuccsess),
                        Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, activity.getString(R.string.movefail),
                        Toast.LENGTH_SHORT).show();
        } else {
            if (success)
                Toast.makeText(activity,
                        activity.getString(R.string.copysuccsess),
                        Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(activity, activity.getString(R.string.copyfail),
                        Toast.LENGTH_SHORT).show();
        }

        ClipBoard.unlock();
        ClipBoard.clear();
        activity.invalidateOptionsMenu();
        if(activity instanceof MainActivity){
            ((MainActivity)activity).pasteFinish();
        }

        if (!failed.isEmpty()) {
            Toast.makeText(activity, activity.getString(R.string.cantopenfile),
                    Toast.LENGTH_SHORT).show();
            if (!activity.isFinishing()) {
                dialog.show();
            }
        }
    }
}