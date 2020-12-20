//package com.akahlouts.todo_list;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class PopupForListActivity extends AppCompatActivity {
//
//    public static final String EXTRA_REPLY = "com.akahlouts.android.reply";
//    private EditText itemNameOfList;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.popupforlist);
//
//        itemNameOfList = findViewById(R.id.aListItem);
//
//        final Button btn_saveList = findViewById(R.id.btn_saveList);
//        btn_saveList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent replyIntent = new Intent();
//                if (TextUtils.isEmpty(itemNameOfList.getText())) {
//                    setResult(RESULT_CANCELED, replyIntent);
//                }else {
//                    String nodoString = itemNameOfList.getText().toString();
//                    replyIntent.putExtra(EXTRA_REPLY, nodoString);
//                    setResult(RESULT_OK, replyIntent);
//                }
//                finish();
//            }
//        });
//    }
//}
