package com.example.aexpress.fragments;

import static java.util.Spliterator.DISTINCT;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aexpress.R;
import com.example.aexpress.databinding.FragmentChatBinding;
import com.sendbird.android.SendbirdChat;
import com.sendbird.android.channel.GroupChannel;
import com.sendbird.android.channel.OpenChannel;
import com.sendbird.android.exception.SendbirdException;
import com.sendbird.android.handler.ConnectHandler;
import com.sendbird.android.handler.GroupChannelCallbackHandler;
import com.sendbird.android.handler.InitResultHandler;
import com.sendbird.android.handler.OpenChannelCallbackHandler;
import com.sendbird.android.params.GroupChannelCollectionCreateParams;
import com.sendbird.android.params.GroupChannelCreateParams;
import com.sendbird.android.params.InitParams;
import com.sendbird.android.params.OpenChannelCreateParams;
import com.sendbird.android.user.User;

import java.util.UUID;


public class FragmentChat extends Fragment {

    private final  String APP_ID="EE3E98AD-CCDB-4234-9510-23AF715F9FE4";
    private FragmentChatBinding binding;

    public FragmentChat() {

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setTitle("COMING SOON...");
        dialog.setMessage("features in development please wait...");
        dialog.show();
        final String USER_ID = UUID.randomUUID().toString();
        final  String CHANNEL_ID = UUID.randomUUID().toString();


        SendbirdChat.init(new InitParams(APP_ID,getContext(),false), new InitResultHandler() {
            @Override
            public void onMigrationStarted() {

            }

            @Override
            public void onInitFailed(SendbirdException e) {

            }

            @Override
            public void onInitSucceed() {

            }
        });
        SendbirdChat.connect(USER_ID, new ConnectHandler() {
            @Override
            public void onConnected(User user, SendbirdException e) {
                if (e == null) {
                    // Kết nối và đăng nhập thành công
                } else {
                    // Xử lý lỗi kết nối và đăng nhập
                }
            }
        });

        OpenChannelCreateParams openChannelCreateParams = new OpenChannelCreateParams();
        openChannelCreateParams.setName("Customer");

        OpenChannel.createChannel(openChannelCreateParams, new OpenChannelCallbackHandler() {
            @Override
            public void onResult(OpenChannel openChannel, SendbirdException e) {
                if (e == null) {
                    // Tạo phòng chat thành công
                } else {
                    // Xử lý lỗi khi tạo phòng chat
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }



}