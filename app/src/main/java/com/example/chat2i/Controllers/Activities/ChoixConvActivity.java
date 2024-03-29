package com.example.chat2i.Controllers.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.chat2i.Models.Conversation;
import com.example.chat2i.R;
import com.example.chat2i.Utils.GlobalState;
import com.example.chat2i.Utils.VolleyCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.lang.reflect.Type;
import java.util.List;

public class ChoixConvActivity extends DefaultActivity implements View.OnClickListener {

    private GlobalState gs;

    private Button btnOK;
    private Spinner sp;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gs = (GlobalState) getApplication();
        setContentView(R.layout.activity_choix_conversation);

        sp = (Spinner) findViewById(R.id.choixConversation_choixConv);

        // Au démarrage de l'activité, réaliser une requete
        //  pour récupérer les conversations
        String qs = "action=getConversations";

        // On se sert des services offerts par librairie Volley
        //  pour effectuer nos requêtes
        gs.volleyJsonObjectRequest(qs,
                new VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        gs.log(response.toString());
                        displayConversation(response);
                    }
                });

        btnOK = findViewById(R.id.choixConversation_btnOK);
        btnOK.setOnClickListener(this);
    }

    public void displayConversation(JSONObject response) {
        JSONArray convs = null;
        List<Conversation> conversationList = new ArrayList<Conversation>();
        try {
            //on récupère les conversations dans le JSON
            convs = response.getJSONArray("conversations");

            //on récupère le type avec TypeToken. Elle va permettre à la librairie de connaitre
            //le type de retour de notre list car il ne peut pas être déterminé à l'execution
            Type listType = new TypeToken<ArrayList<Conversation>>(){}.getType();
            //on crée le Gson qui va transformer le JSONArray en objet Conversation qui sera placé dans une List<Conversation>
            conversationList = new Gson().fromJson(String.valueOf(convs), listType);

            //parcours de la List<Conversation> pour afficher comme les conversations dans le log
            for(Conversation c : conversationList){
                gs.log("Conv " + c.getId()  + " / theme = " + c.getTheme() + " / active ?" + c.getActive());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        gs.log(conversationList.toString());

        // On peut maintenant appuyer sur le bouton
        btnOK.setEnabled(true);
        sp.setAdapter(new MyCustomAdapter(this,
                R.layout.spinner_item,
                (ArrayList<Conversation>) conversationList));
    }

    @Override
    public void onClick(@NonNull View v) {
        Conversation convSelected = (Conversation) sp.getSelectedItem();
        gs.log("Conv sélectionnée : " + convSelected.getTheme()
                        + " id=" + convSelected.getId());

        // On crée un Intent pour changer d'activité
        Intent toShowConv = new Intent(this, ShowConvActivity.class);
        Bundle bdl = new Bundle();
        bdl.putInt("idConversation",convSelected.getId());
        toShowConv.putExtras(bdl);
        startActivity(toShowConv);
    }


    public class MyCustomAdapter extends ArrayAdapter<Conversation> {
        private int layoutId;
        private ArrayList<Conversation> dataConvs;

        public MyCustomAdapter(@NonNull Context context,
                               int itemLayoutId,
                               @NonNull ArrayList<Conversation> objects) {
            super(context, itemLayoutId, objects);
            layoutId = itemLayoutId;
            dataConvs = objects;
        }

        @Override
        @NonNull
        public View getDropDownView(@NonNull int position, View convertView, ViewGroup parent) {
            //return getCustomView(position, convertView, parent);
            LayoutInflater inflater = getLayoutInflater();
            View item = inflater.inflate(layoutId, parent, false);
            Conversation nextC = dataConvs.get(position);

            TextView label = (TextView) item.findViewById(R.id.spinner_theme);
            label.setText(nextC.getTheme());

            ImageView icon = (ImageView) item.findViewById(R.id.spinner_icon);

            if (nextC.getActive()) {
                icon.setImageResource(R.drawable.conv_active);
            } else {
                icon.setImageResource(R.drawable.conv);
            }

            return item;
        }

        @Override
        @NonNull
        public View getView(@NonNull  int position, View convertView, ViewGroup parent) {
            //return getCustomView(position, convertView, parent);
            LayoutInflater inflater = getLayoutInflater();
            View item = inflater.inflate(layoutId, parent, false);
            Conversation nextC = dataConvs.get(position);

            TextView label = (TextView) item.findViewById(R.id.spinner_theme);
            label.setText(nextC.getTheme());

            ImageView icon = (ImageView) item.findViewById(R.id.spinner_icon);

            if (nextC.getActive()) {
                icon.setImageResource(R.drawable.conv_active);
            } else {
                icon.setImageResource(R.drawable.conv);
            }

            return item;
        }
    }

}
