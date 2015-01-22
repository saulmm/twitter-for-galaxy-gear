/*    
 * Copyright (c) 2014 Samsung Electronics Co., Ltd.   
 * All rights reserved.   
 *   
 * Redistribution and use in source and binary forms, with or without   
 * modification, are permitted provided that the following conditions are   
 * met:   
 *   
 *     * Redistributions of source code must retain the above copyright   
 *        notice, this list of conditions and the following disclaimer.  
 *     * Redistributions in binary form must reproduce the above  
 *       copyright notice, this list of conditions and the following disclaimer  
 *       in the documentation and/or other materials provided with the  
 *       distribution.  
 *     * Neither the name of Samsung Electronics Co., Ltd. nor the names of its  
 *       contributors may be used to endorse or promote products derived from  
 *       this software without specific prior written permission.  
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS  
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT  
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR  
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT  
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,  
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY  
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT  
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE  
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package tweetgear.com.saulmm.wearables;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.HashMap;

import javax.security.cert.X509Certificate;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAAuthenticationToken;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;

import tweetgear.com.saulmm.executor.JobExecutor;
import tweetgear.com.saulmm.model.Tweet;
import tweetgear.com.saulmm.use_cases.FavoriteUsecase;
import tweetgear.com.saulmm.use_cases.FavoriteUsecaseImpl;
import tweetgear.com.saulmm.use_cases.GetTweetsUseCase;
import tweetgear.com.saulmm.use_cases.GetTweetsUseCaseImpl;
import tweetgear.com.saulmm.use_cases.RetweetUsecase;
import tweetgear.com.saulmm.use_cases.RetweetUsecaseImpl;
import twitter4j.Twitter;

public class CommService extends SAAgent {

    public Context mContext = null;

    public static final int HELLOACCESSORY_CHANNEL_ID = 104;
    private HashMap<Integer, CommServiceProviderConnection> mConnectionsMap;

    private Twitter twitterClient;
    private IBinder mBinder;
    private int mConnectionId;

    public void setTwitterClient(Twitter twitterClient) {

        this.twitterClient = twitterClient;
    }

    public class LocalBinder extends Binder {

        public CommService getService() {

            return CommService.this;
        }
    }

    public CommService() {

        super("[DEBUG]", CommServiceProviderConnection.class);
        mBinder = new LocalBinder();
    }

    public class CommServiceProviderConnection extends SASocket {

        public CommServiceProviderConnection() {

            super(CommServiceProviderConnection.class.getName());
        }

        @Override
        public void onReceive(int channelId, byte[] data) {

            if (twitterClient == null) {

                uHandler = mConnectionsMap.get(Integer.parseInt(String.valueOf(mConnectionId)));
                sendMessageToGear(uHandler, "/error/not_logged");
                return;
            }

            String receivedString = new String (data);

            if (receivedString.equals("/twitter/get_tweets")) {

                GetTweetsUseCase getTweetsUsecase = new GetTweetsUseCaseImpl(twitterClient, getTweetsCallback);
                JobExecutor.getInstance().execute(getTweetsUsecase);

            } else if (receivedString.startsWith("/twitter/retweet/")) {

                Log.d("[DEBUG]", "CommServiceProviderConnection onReceive - ");
                String tweetID = receivedString.split("/")[3];
                Log.d("[DEBUG]", "CommServiceProviderConnection onReceive - Tweet ID "+tweetID);
                RetweetUsecase retweetUsecase = new RetweetUsecaseImpl(twitterClient, tweetID, retweetCallback);
                JobExecutor.getInstance().execute(retweetUsecase);

            } else if (receivedString.startsWith("/twitter/favorite/")) {

                Log.d("[DEBUG]", "CommServiceProviderConnection onReceive - ");
                String tweetID = receivedString.split("/")[3];
                FavoriteUsecase favoriteUsecase = new FavoriteUsecaseImpl(twitterClient, tweetID, favoriteCallback);
                JobExecutor.getInstance().execute(favoriteUsecase);
            }
        }

        @Override
        protected void onServiceConnectionLost(int errorCode) {

            if (mConnectionsMap != null) {

                Log.d("[DEBUG]", "HelloAccessoryProviderConnection onServiceConnectionLost -" +
                        " Connection lost: "+errorCode);

                mConnectionsMap.remove(mConnectionId);
            }
        }

        @Override
        public void onError(int channelId, String errorString, int error) {

            Log.e("[ERROR]", "CommServiceProviderConnection onError - "+errorString);
        }
    }

    @Override
    public void onCreate() {

        super.onCreate();
        SA mAccessory = new SA();

        try {

            mAccessory.initialize(this);

        } catch (SsdkUnsupportedException e) {

            Log.d("[DEBUG]", "HelloAccessoryProviderService onCreate -" +
                    " SDK Unsupported: "+e.getMessage());

        } catch (Exception e1) {
            Log.d("[DEBUG]", "HelloAccessoryProviderService onCreate - " +
                    "Cannot use Accesory package or Mobile SDK: "+e1.getMessage());

            e1.printStackTrace();
            stopSelf();
        }
    }

    @Override
    protected void onFindPeerAgentResponse(SAPeerAgent arg0, int arg1) {}

    @Override
    protected void onServiceConnectionResponse(SAPeerAgent peerAgent, SASocket thisConnection,int result) {

        if (result == CONNECTION_SUCCESS) {

            if (thisConnection != null) {
                CommServiceProviderConnection myConnection = (CommServiceProviderConnection) thisConnection;

                if (mConnectionsMap == null)
                    mConnectionsMap = new HashMap<Integer, CommServiceProviderConnection>();

                mConnectionId = (int) (System.currentTimeMillis() & 255);
                mConnectionsMap.put(mConnectionId, myConnection);
            }

        } else if (result == CONNECTION_ALREADY_EXIST) {

            Log.d("[DEBUG]", "HelloAccessoryProviderService onServiceConnectionResponse " +
                    "- CONNECTION_ALREADY_EXIST");
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {

        return mBinder;
    }

    protected void onAuthenticationResponse(SAPeerAgent uPeerAgent, SAAuthenticationToken authToken, int error) {

        if (authToken.getAuthenticationType() == SAAuthenticationToken.AUTHENTICATION_TYPE_CERTIFICATE_X509) {

            mContext = getApplicationContext();
            byte[] myAppKey = getApplicationCertificate(mContext);

            if (authToken.getKey() != null) {

                boolean matched = true;

                if(authToken.getKey().length != myAppKey.length){
                    matched = false;

                } else {

                    for(int i=0; i<authToken.getKey().length; i++) {

                        if(authToken.getKey()[i]!=myAppKey[i]) {
                            matched = false;
                        }
                    }
                }

                if (matched) {
                    acceptServiceConnectionRequest(uPeerAgent);
                }
            }

        } else if (authToken.getAuthenticationType() == SAAuthenticationToken.AUTHENTICATION_TYPE_NONE) {

            Log.e("[ERROR]", "HelloAccessoryProviderService onAuthenticationResponse - CERT_TYPE(NONE)");
        }
    }

    private static byte[] getApplicationCertificate(Context context) {

        if(context == null) {
            return null;
        }

        Signature[] sigs;
        byte[] certificat = null;
        String packageName = context.getPackageName();


        try {

            PackageInfo pkgInfo = null;
            pkgInfo = context.getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);

            if (pkgInfo == null)
                return null;

            sigs = pkgInfo.signatures;

            if (sigs != null) {

                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                ByteArrayInputStream stream = new ByteArrayInputStream(sigs[0].toByteArray());
                X509Certificate cert;
                cert = X509Certificate.getInstance(stream);
                certificat = cert.getPublicKey().getEncoded();
            }

        } catch (NameNotFoundException e) {

            Log.e("[ERROR]", "HelloAccessoryProviderService getApplicationCertificate -" +
                    " "+e.getMessage());

            e.printStackTrace();

        } catch (CertificateException e) {

            Log.e("[ERROR]", "HelloAccessoryProviderService getApplicationCertificate -" +
                    " " +e.getMessage());

            e.printStackTrace();

        } catch (javax.security.cert.CertificateException e) {

            Log.e("[ERROR]", "HelloAccessoryProviderService getApplicationCertificate -" +
                    " " +e.getMessage());

            e.printStackTrace();
        }

        return certificat;
    }

    private boolean sendMessageToGear (final CommServiceProviderConnection connectionHandler, final String message) {

        new Thread(new Runnable() {
            public void run() {

                try {
                    connectionHandler.send(HELLOACCESSORY_CHANNEL_ID, message.getBytes());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        return true;
    }

    private CommServiceProviderConnection uHandler;
    private GetTweetsUseCase.Callback getTweetsCallback = new GetTweetsUseCase.Callback() {

        @Override
        public void onTweetsListLoaded(Collection<Tweet> tweetsCollection) {

            final String compressedTweets = Tweet.getCompressedTweets(tweetsCollection);

             uHandler = mConnectionsMap.get(Integer.parseInt(String.valueOf(mConnectionId)));

            if(uHandler != null) {

                String order = "__tw";
                sendMessageToGear(uHandler, order + "|=|"+ compressedTweets);

            } else {

                Log.e("[ERROR]", "CommService onTweetsListLoaded - Null communication");
            }
        }


        @Override
        public void onError(String error) {

            Log.e("[ERROR]", "CommService onError - "+error);
        }
    };

    private RetweetUsecase.Callback retweetCallback = new RetweetUsecase.Callback() {

        @Override
        public void onRetweetSuccess() {
            Log.d("[DEBUG]", "CommService onRetweetSuccess - Success");
        }

        @Override
        public void onError(String error) {

            Log.d("[DEBUG]", "CommService onError - Fail "+error);
        }
    };

    private FavoriteUsecase.Callback favoriteCallback = new FavoriteUsecase.Callback() {
        @Override
        public void onFavoriteSuccess() {

            Log.d("[DEBUG]", "CommService onFavoriteSuccess - Success");
        }

        @Override
        public void onFavoriteError() {

            Log.e("[ERROR]", "CommService onFavoriteError - Favorite error");

        }
    };
}