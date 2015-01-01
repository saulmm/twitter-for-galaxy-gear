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
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAAuthenticationToken;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;

import tweetgear.com.saulmm.executor.JobExecutor;
import tweetgear.com.saulmm.model.Tweet;
import tweetgear.com.saulmm.use_cases.GetTweetsUseCase;
import tweetgear.com.saulmm.use_cases.GetTweetsUseCaseImpl;
import twitter4j.Twitter;

public class CommService extends SAAgent {
    public static final String TAG = "HelloAccessoryProviderService";

    public Context mContext = null;

    public static final int HELLOACCESSORY_CHANNEL_ID = 104;

    HashMap<Integer, CommServiceProviderConnection> mConnectionsMap = null;

    private final IBinder mBinder = new LocalBinder();

    private Twitter twitterClient;

    public void setTwitterClient(Twitter twitterClient) {

        this.twitterClient = twitterClient;
    }

    public class LocalBinder extends Binder {

        public CommService getService() {

            return CommService.this;
        }
    }

    public CommService() {

        super(TAG, CommServiceProviderConnection.class);
    }

    public class CommServiceProviderConnection extends SASocket {

        private int mConnectionId;

        public CommServiceProviderConnection() {

            super(CommServiceProviderConnection.class.getName());
        }

        @Override
        public void onError(int channelId, String errorString, int error) {

            Log.e("[ERROR]", "CommServiceProviderConnection onError - "+errorString);
        }

        @Override
        public void onReceive(int channelId, byte[] data) {

            final String[] tweets = {""};


            GetTweetsUseCase getTweetsUseCase = new GetTweetsUseCaseImpl(twitterClient, new GetTweetsUseCase.Callback() {
                @Override
                public void onTweetsListLoaded(Collection<Tweet> tweetsCollection) {

                    String fieldSeparator = "-.__";
                    String tweetSeparator = "|_|";

                    for (Tweet tweet : tweetsCollection) {

                        tweets[0] += tweet.getUsername();
                        tweets[0] += fieldSeparator;
                        tweets[0] += tweet.getTime();
                        tweets[0] += fieldSeparator;
                        tweets[0] += "@"+tweet.getUsername();
                        tweets[0] += fieldSeparator;
                        tweets[0] += tweet.getText();

                        tweets[0] += tweetSeparator;

                    }

                    final CommServiceProviderConnection uHandler = mConnectionsMap.get(
                            Integer.parseInt(String.valueOf(mConnectionId)));

                    if(uHandler == null) {

                        Log.e("[ERROR]", "HelloAccessoryProviderConnection onReceive " +
                                "- Connection Handler null");

                        return;
                    }

                    // Send the message
                    new Thread(new Runnable() {

                        public void run() {

                            try {
                                uHandler.send(HELLOACCESSORY_CHANNEL_ID, tweets[0].getBytes());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                }

                @Override
                public void onError(String error) {

                    Log.d("[DEBUG]", "CommServiceProviderConnection onError - Error: "+error);
                }
            });

            JobExecutor.getInstance().execute(getTweetsUseCase);


            final String message = tweets[0];

//            final String message = ""+
//                    "Padre lol-.__6 min-.__@_saulmm-.__This is an awesome sample tweet by a @dummie user, the @dummie user is talking about #things of the real life"+"|_|"+
//                    "Yoko Ono-.__1 min-.__@_yoko-.__The split() method is used to split a string into an array of substrings, and returns the new array.Tip: If an empty string () is used as the separator, the string is split between each character."+"|_|"+
//                    "TechChrunch-.__38 min-.__@TechCrunch-.__We don’t have to live with income inequality if we can design the right economic structures to ultimately reverse it http://tcrn.ch/1xvH4tu "+
//                    "Saul M-.__6 min-.__@_saulmm-.__This is an awesome sample tweet by a @dummie user, the @dummie user is talking about #things of the real life"+"|_|"+
//                    "Yoko Ono-.__1 min-.__@_yoko-.__The split() method is used to split a string into an array of substrings, and returns the new array.Tip: If an empty string () is used as the separator, the string is split between each character."+"|_|"+
//                    "TechChrunch-.__38 min-.__@TechCrunch-.__We don’t have to live with income inequality if we can design the right economic structures to ultimately reverse it http://tcrn.ch/1xvH4tu "+
//                    "Saul M-.__6 min-.__@_saulmm-.__This is an awesome sample tweet by a @dummie user, the @dummie user is talking about #things of the real life"+"|_|"+
//                    "Yoko Ono-.__1 min-.__@_yoko-.__The split() method is used to split a string into an array of substrings, and returns the new array.Tip: If an empty string () is used as the separator, the string is split between each character."+"|_|"+
//                    "TechChrunch-.__38 min-.__@TechCrunch-.__We don’t have to live with income inequality if we can design the right economic structures to ultimately reverse it http://tcrn.ch/1xvH4tu "+
//                    "Saul M-.__6 min-.__@_saulmm-.__This is an awesome sample tweet by a @dummie user, the @dummie user is talking about #things of the real life"+"|_|"+
//                    "Yoko Ono-.__1 min-.__@_yoko-.__The split() method is used to split a string into an array of substrings, and returns the new array.Tip: If an empty string () is used as the separator, the string is split between each character."+"|_|"+
//                    "TechChrunch-.__38 min-.__@TechCrunch-.__We don’t have to live with income inequality if we can design the right economic structures to ultimately reverse it http://tcrn.ch/1xvH4tu "+
//                    "Saul M-.__6 min-.__@_saulmm-.__This is an awesome sample tweet by a @dummie user, the @dummie user is talking about #things of the real life"+"|_|"+
//                    "Yoko Ono-.__1 min-.__@_yoko-.__The split() method is used to split a string into an array of substrings, and returns the new array.Tip: If an empty string () is used as the separator, the string is split between each character."+"|_|"+
//                    "TechChrunch-.__38 min-.__@TechCrunch-.__We don’t have to live with income inequality if we can design the right economic structures to ultimately reverse it http://tcrn.ch/1xvH4tu "+
//                    "Saul M-.__6 min-.__@_saulmm-.__This is an awesome sample tweet by a @dummie user, the @dummie user is talking about #things of the real life"+"|_|"+
//                    "Yoko Ono-.__1 min-.__@_yoko-.__The split() method is used to split a string into an array of substrings, and returns the new array.Tip: If an empty string () is used as the separator, the string is split between each character."+"|_|"+
//                    "TechChrunch-.__38 min-.__@TechCrunch-.__We don’t have to live with income inequality if we can design the right economic structures to ultimately reverse it http://tcrn.ch/1xvH4tu "+
//                    "Saul M-.__6 min-.__@_saulmm-.__This is an awesome sample tweet by a @dummie user, the @dummie user is talking about #things of the real life"+"|_|"+
//                    "Yoko Ono-.__1 min-.__@_yoko-.__The split() method is used to split a string into an array of substrings, and returns the new array.Tip: If an empty string () is used as the separator, the string is split between each character."+"|_|"+
//                    "TechChrunch-.__38 min-.__@TechCrunch-.__We don’t have to live with income inequality if we can design the right economic structures to ultimately reverse it http://tcrn.ch/1xvH4tu ";



        }

        @Override
        protected void onServiceConnectionLost(int errorCode) {

            if (mConnectionsMap != null) {

                Log.d("[DEBUG]", "HelloAccessoryProviderConnection onServiceConnectionLost -" +
                        " Connection lost: "+errorCode);

                mConnectionsMap.remove(mConnectionId);
            }
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



    protected void onAuthenticationResponse(SAPeerAgent uPeerAgent,
                                            SAAuthenticationToken authToken, int error) {

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

    @Override
    protected void onFindPeerAgentResponse(SAPeerAgent arg0, int arg1) {}


    @Override
    protected void onServiceConnectionResponse(SAPeerAgent peerAgent,
                                               SASocket thisConnection,int result) {

        if (result == CONNECTION_SUCCESS) {

            if (thisConnection != null) {
                CommServiceProviderConnection myConnection = (CommServiceProviderConnection) thisConnection;

                if (mConnectionsMap == null)
                    mConnectionsMap = new HashMap<Integer, CommServiceProviderConnection>();

                myConnection.mConnectionId = (int) (System.currentTimeMillis() & 255);
                mConnectionsMap.put(myConnection.mConnectionId, myConnection);
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
}