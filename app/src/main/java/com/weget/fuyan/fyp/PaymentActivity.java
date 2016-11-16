package com.weget.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnTextChanged;


public class PaymentActivity extends AppCompatActivity {


    private static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char CARD_NUMBER_DIVIDER = '-';

    private static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    private static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    private static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    private static final char CARD_DATE_DIVIDER = '/';

    private static final int CARD_CVC_TOTAL_SYMBOLS = 3;

    private String clientToken;
    private static final int REQUEST_CODE = Menu.FIRST;
    String username, password, authString, priceS, requestString, stripeCode, priceCentS, err, productName, productDesc;
    private Context mContext;
    int fulfillId;
    double price;
    String cardNum;
    String cardExpDate;
    String cvcNum;
    EditText cardNumET,cardExpDateET, cardCvcET;
    int cardMonth, cardYear;
    Button paymentBtn;
    //pk_test_Jmx4WCkGv8XuSMz2NFSx3HEC
    //pk_live_aNklK47hNoxf21zHI5qY0TsI
    final String TEST_API = "pk_test_Jmx4WCkGv8XuSMz2NFSx3HEC";
    String URL;
    TextView productNameTV, productDescTV, productTitleTV, valTV, totalTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_payment);



        ButterKnife.bind(this);

        URL = getString(R.string.webserviceurl);
        fulfillId = getIntent().getIntExtra("fulfill_Id", 0);
        price = getIntent().getDoubleExtra("fulfill_price",0);
        productName = getIntent().getStringExtra("product_name");
        productDesc = getIntent().getStringExtra("product_desc");
        requestString = getIntent().getStringExtra("request_string");
        priceS = String.valueOf(price);



        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        authString  = username + ":" + password;



        cardNumET = (EditText)findViewById(R.id.cardNumberEditText);
        cardExpDateET = (EditText)findViewById(R.id.cardDateEditText);
        cardCvcET = (EditText)findViewById(R.id.cardCVCEditText);
        paymentBtn = (Button)findViewById(R.id.btn_start);
        paymentBtn.setTransformationMethod(null);



        productNameTV = (TextView)findViewById(R.id.product_name);
        productDescTV = (TextView)findViewById(R.id.product_description);
        productTitleTV = (TextView)findViewById(R.id.title);
        valTV = (TextView)findViewById(R.id.val);
//        totalTV = (TextView)findViewById(R.id.total);

        productNameTV.setText(productName);
        productDescTV.setText(productDesc);
        valTV.setText("$" + priceS + "0");

        //set fonts
//        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
//        productNameTV.setTypeface(typeFace);
//        productDescTV.setTypeface(typeFace);
//        productTitleTV.setTypeface(typeFace);
//        valTV.setTypeface(typeFace);
//        totalTV.setTypeface(typeFace);

        cardNum = cardNumET.getText().toString();

        cardNumET.addTextChangedListener(new TextWatcher(){
            // Change this to what you want... ' ', '-' etc..
            private static final char space = ' ';

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Remove spacing char
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                // Insert char where needed.
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                    }
                }
            }
        });



        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                cardNum = cardNumET.getText().toString();
                cardExpDate = cardExpDateET.getText().toString();
                cvcNum = cardCvcET.getText().toString();
                priceCentS = String.valueOf(price*100).substring(0,String.valueOf(price*100).indexOf('.'));

                Log.d("Card Num:=======",cardNum );
                if(cardNum.trim()!=null && cardNum.trim().length()!= 0){
                    if(cardExpDate.trim()!=null && cardExpDate.trim().length()!=0){
                        if(cvcNum.trim()!=null&& cvcNum.trim().length()!=0){

                            cardMonth = Integer.parseInt(cardExpDate.substring(0,cardExpDate.indexOf('/')));
                            cardYear= 2000 + Integer.parseInt(cardExpDate.substring(cardExpDate.indexOf('/')+1));

                            Log.d ("Year:==========", ""+cardYear);
                            Card card = new Card(cardNum,cardMonth, cardYear, cvcNum);
                            if (card.validateCard()) {
                                // Do payment (create Charge)
                                Log.d("CARD:=========", "IS VALID!");

                                try {
                                    Stripe stripe = new Stripe(TEST_API);

                                    stripe.createToken(
                                            card,
                                            new TokenCallback() {
                                                public void onSuccess(Token token) {
                                                    // Send token to your server
                                                    Log.d ("Card Token:========= ", token.getId());
                                                    Log.d ("Request ID:==========", requestString);
                                                    Log.d ("Price:==========", priceCentS);

                                                    new processPayment().execute(token.getId());
                                                }
                                                public void onError(Exception error) {
                                                    // Show localized error message
                                                    /*
                                                    Toast.makeText(mContext,
                                                            error.getLocalizedString(mContext),
                                                            Toast.LENGTH_LONG
                                                    ).show();
                                                    */
                                                }
                                            }
                                    );
                                }catch(AuthenticationException e){
                                    e.printStackTrace();
                                }
                            }else{
                                //Show error

                                if(!card.validateNumber()){
                                    cardNumET.setError("Invalid Card Number!");
                                }
                                if(!card.validateExpiryDate()){
                                    cardExpDateET.setError("Invalid Expiry Date!");
                                }
                                if(!card.validateCVC()){
                                    cardCvcET.setError("Invalid CVC!");
                                }
                            }
                        }else{
                            cardCvcET.setError("Invalid CVC!");

                        }
                    }else{
                        cardExpDateET.setError("Invalid Expiry Date!");
                    }
                }else{
                    cardNumET.setError("Invalid Card Number!");
                }


            }
        });




        //new processToken().execute(authString);

    }

    @OnTextChanged(value = R.id.cardNumberEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardNumberTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_NUMBER_TOTAL_SYMBOLS, CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_NUMBER_TOTAL_DIGITS), CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER));
        }
    }

    @OnTextChanged(value = R.id.cardDateEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardDateTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_DATE_TOTAL_SYMBOLS, CARD_DATE_DIVIDER_MODULO, CARD_DATE_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_DATE_TOTAL_DIGITS), CARD_DATE_DIVIDER_POSITION, CARD_DATE_DIVIDER));
        }
    }

    @OnTextChanged(value = R.id.cardCVCEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardCVCTextChanged(Editable s) {
        if (s.length() > CARD_CVC_TOTAL_SYMBOLS) {
            s.delete(CARD_CVC_TOTAL_SYMBOLS, s.length());
        }
    }



    private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }

    private class processPayment extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(PaymentActivity.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            findViewById(R.id.btn_start).setEnabled(false);
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);

            if(!isFinishing()) {
                dialog.show();
            }

        }


        @Override
        protected Boolean doInBackground(String... params) {

            boolean success = false;
            final String basicAuth = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
            String url = URL + "checkout/";

            JSONObject jsoin = null;

            try {
                jsoin = new JSONObject();
                jsoin.put("requestId", requestString);
                jsoin.put("amount", priceCentS);
                jsoin.put("token", params[0]);


            } catch(JSONException e) {
                e.printStackTrace();
                err = e.getMessage();
            }

            String rst = UtilHttp.doHttpPostBasicAuthentication(mContext, url, jsoin.toString()+ basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {
                success = true;

                try{

                    JSONObject jso = new JSONObject(rst);
                    stripeCode = jso.getString("message");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return success;
        }

        protected void onPostExecute(Boolean result) {

            dialog.dismiss();
            if(result) {

                new acceptFulfill().execute(authString);
               // Toast.makeText(getBaseContext(), "Transaction Complete!", Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
                findViewById(R.id.btn_start).setEnabled(true);
            }

        }


    }

    private class acceptFulfill extends AsyncTask<String, Void, Boolean> {
        private int transactionId, requestId;
        boolean received, delivered;
        double amount;
        String status;
        Transaction tr;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "fulfill/" + fulfillId + "/";

            JSONObject jsoin = null;

            try {
                jsoin = new JSONObject();
                jsoin.put("stripeCode", stripeCode);


            } catch(JSONException e) {
                e.printStackTrace();
                err = e.getMessage();
            }

            String rst = UtilHttp.doHttpPutBasicAuthentication(mContext, url, jsoin.toString()+ basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
            } else {
                success = true;

//                JSONObject jso = null;
                try{
//                    jso = new JSONObject(rst);
//
//                    transactionId = jso.getInt("transactionId");
//                    requestId = jso.getInt("requestId");
//                    fulfillId = jso.getInt("fulfillId");
//                    amount = jso.getDouble("amount");
//                    stripeCode = jso.getString("stripeCode");
//                    received = jso.getBoolean("received");
//                    delivered = jso.getBoolean("delivered");
//                    status = jso.getString("status");
//                    tr = new Transaction (transactionId, requestId, fulfillId, amount, stripeCode,
//                            received, delivered, status);

                Gson gson = new Gson();
                tr = gson.fromJson(rst, Transaction.class);

                }catch(Exception e){
                    e.printStackTrace();
                    err = e.getMessage();
                }


            }
            return success;


        }
        @Override
        protected void onPostExecute(Boolean result) {

            if(result) {
                //Toast.makeText(getBaseContext(), "Transaction Created!", Toast.LENGTH_LONG).show();
                Intent i = new Intent(PaymentActivity.this, MainActivity.class);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.putExtra("after_payment_request_tab", 1);
                i.putExtra("after_payment_request_swipe", 1);

                //i.putExtra("transaction", (Serializable)tr);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
            }

        }
    }

}
