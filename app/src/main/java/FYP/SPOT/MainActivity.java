package FYP.SPOT;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import FYP.SPOT.Adapter.ChatmessageAdapter;
import FYP.SPOT.Model.ChatMessage;

public class MainActivity extends AppCompatActivity {


    ListView listView;
    FloatingActionButton btnSend;
    EditText edtTextMag;
    ImageView imageView;

    private Bot bot;
    public static Chat chat;
    private ChatmessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        btnSend = findViewById(R.id.btnSend);
        edtTextMag = findViewById(R.id.edtTextMsg);
        imageView = findViewById(R.id.imageView);

        adapter = new ChatmessageAdapter(this,new ArrayList<ChatMessage>());
        listView.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtTextMag.getText().toString();
                String response = chat.multisentenceRespond(edtTextMag.getText().toString());
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(MainActivity.this,"Please enter a query", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage(message);
                botsReply(response);

                edtTextMag.setText("");
                listView.setSelection(adapter.getCount() - 1);
            }
        });

        Dexter.withContext(getApplicationContext())
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    custom();
                    Toast.makeText(MainActivity.this, "Permission Granted..!", Toast.LENGTH_SHORT).show();
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    Toast.makeText(MainActivity.this, "Please Grant all the Premissions!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError dexterError) {
                Toast.makeText(MainActivity.this, ""+dexterError, Toast.LENGTH_SHORT).show();
            }
        }).onSameThread().check();

        // get the working directory
        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/TBC";
        AIMLProcessor.extension = new PCAIMLProcessorExtension();

        bot = new Bot("TBC", MagicStrings.root_path, "chat");
        chat = new Chat(bot);
    }

    private void custom() {
        boolean available = isSDCartAvailable();

        AssetManager assets = getResources().getAssets();
        File fileName = new File(Environment.getExternalStorageDirectory().toString() + "/TBC/bots/TBC");

        boolean makeFile = fileName.mkdirs();

        // read the aiml file
        if (fileName.exists()){
            try{
                for(String dir: assets.list("TBC")){
                    File subDir = new File (fileName.getPath() + "/" + dir);
                    boolean subDir_Check = subDir.mkdirs();
                    for (String file: assets.list("TBC/"+ dir)){
                        File newFile = new File(fileName.getPath() + "/" + dir + "/" + file);

                        if (newFile.exists()){
                            continue;
                        }
                        InputStream in;
                        OutputStream out;
                        String str;
                        in = assets.open("TBC/" + dir + "/" + file);
                        out = new FileOutputStream(fileName.getPath() + "/" + dir + "/" +file);

                        //copy files from assets to the mobile phone's SD card or any secondary storage
                        copyFile(in,out);
                        in.close();
                        out.flush();
                        out.close();
                    }
                }
            } catch (IOException e){
                e.getStackTrace();
            }
        }
    }

    //copy the files
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte [1024];
        int read;
        while ((read = in.read(buffer)) != -1){
            out.write(buffer, 0,read);
        }
    }

    private static boolean isSDCartAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)? true : false;
    }

    private void botsReply(String response){
        ChatMessage chatMessage = new ChatMessage(false,false,response);
        adapter.add(chatMessage);
    }

    private void sendMessage(String message){
        ChatMessage chatMessage = new ChatMessage(false, true, message);
        adapter.add(chatMessage);
    }
}
