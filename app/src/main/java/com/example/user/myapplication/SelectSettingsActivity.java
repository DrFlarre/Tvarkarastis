package com.example.user.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SelectSettingsActivity extends AppCompatActivity {

    LinearLayout priminimai_main, vardai_main, apie_main;
    ImageView vardai_button, priminimai_button, apie_button;
    SharedPreferences mPrefs;
    boolean priminimai, vibracija, panaikinimai;
    int informacija;

    public List<Mokinys> mokiniai = new ArrayList<>();
    public List<Mokinys> rodomiMokiniai = new ArrayList<>();
    public List<Mokinys> pazymetiMokiniai = new ArrayList<>();

    MokiniaiAdapter mAdapter;
    MokiniaiAdapter pazAdapter;

    String string;
    EditText inputName;
    boolean switched;

    boolean keite_nustatymus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("label", 0);
        if(mPrefs.getBoolean("darkTheme", false))
            setTheme(R.style.DarkTheme);

        setContentView(R.layout.activity_select_settings);

        priminimai_main = findViewById(R.id.priminimai_main);
        vardai_main = findViewById(R.id.vardai_main);
        apie_main = findViewById(R.id.apie_main);

        priminimai_button = findViewById(R.id.alarmSettings);
        vardai_button = findViewById(R.id.pasirinktiMokini);
        apie_button = findViewById(R.id.apie);

        if(mPrefs.getBoolean("darkTheme", false)) {
            vardai_button.setImageDrawable(getDrawable(R.drawable.pasirinkti_mokini_white));
            priminimai_button.setImageDrawable(getDrawable(R.drawable.alarm_settings_white));
            apie_button.setImageDrawable(getDrawable(R.drawable.apie_white));
        }

        rodytiIssaugotaSarasa();
    }

    private void rodytiIssaugotaSarasa() {
        String sarasas = mPrefs.getString("nameString", "NULL"), tekstas = mPrefs.getString("issaugotoSarasoTekstas", "NULL");
        if(!sarasas.equals("NULL") && sarasas.equals(tekstas) && mPrefs.getBoolean("switched", false) == mPrefs.getBoolean("issaugotoSarasoSwitched", false)) {
            mokiniai.clear();
            rodomiMokiniai.clear();
            mokiniai = Funkcijos.getList(mPrefs, "mokiniuSarasas");
            rodomiMokiniai = Funkcijos.getList(mPrefs, "mokiniuSarasas");
        }
    }

    public void changeTheme(View view) {
        boolean dark = mPrefs.getBoolean("darkTheme", false);
        dark = !dark;
        mPrefs.edit().putBoolean("darkTheme", dark).apply();

        recreate();
    }




    /*----------------- Perjungti nustatymų skirtukus -------------------------------------*/
    /**Funkcija, atidaranti mokinio pasirinkimo meniu*/
    public void pasirinkti(View view) {

        inputName = findViewById(R.id.inputName);
        inputName.addTextChangedListener(filterTextWatcher);

        switched = mPrefs.getBoolean("switched", false);
        setMokiniaiTextProperties();

        nustatytiSarasa();
        String tempVardai = mPrefs.getString("nameString", "NULL");
        if(mokiniai.size() > 0) {
            mAdapter.notifyDataSetChanged();
            inputName.setHint(R.string.iveskite_varda);
        } else if(!tempVardai.equals("NULL"))
            analyzeNames(tempVardai);

        nustatytiPazymetuSarasa();
        mAdapter.notifyDataSetChanged();

        resetColors(view);
        resetSelection(vardai_main);
    }

    /**Funkcija, atidaranti priminimų nustatymų meniu*/
    public void priminimai(View view) {
        uzkrautiNustatymus();
        resetColors(view);
        resetSelection(priminimai_main);
    }

    void uzkrautiNustatymus() {
        priminimai = mPrefs.getBoolean("priminimai", false);
        vibracija = mPrefs.getBoolean("vibracija", true);
        informacija = mPrefs.getInt("informacija", 0);

        panaikinimai = mPrefs.getBoolean("panaikinimai", false);

        setPriminimaiTextProperties(priminimai);
        changeSettingColor(vibracija, R.id.vibracijosBusena);
        setInformacijaTextProperties(informacija);
        setPanaikinimaiTextProperties();
        setNukirpimoTextProperties();
        changeSettingColor(mPrefs.getBoolean("autoUpdate", true), R.id.autoUpdateBusena);
        changeSettingColor(mPrefs.getBoolean("versionUpdate", true), R.id.versionUpdateBusena);
    }

    /**Funkcija, atidaranti "Apie" meniu*/
    public void apie(View view) {
        resetColors(view);
        resetSelection(apie_main);
    }

    /**Funkcija, padaranti matomą tik nurodytą nustatymų dalį*/
    void resetSelection(LinearLayout ll) {
        apie_main.setVisibility(View.GONE);
        vardai_main.setVisibility(View.GONE);
        priminimai_main.setVisibility(View.GONE);

        ll.setVisibility(View.VISIBLE);
    }

    /**Funkcija, tamsesne spalva pažyminti nurodytą nustatymų dalį*/
    void resetColors(View view) {
        boolean dark = mPrefs.getBoolean("darkTheme", false);
        int col = (!dark) ? ContextCompat.getColor(getApplicationContext(), R.color.defaultItemColor) : ContextCompat.getColor(getApplicationContext(), R.color.defaultItemColor1);
        apie_button.setBackgroundColor(col);
        vardai_button.setBackgroundColor(col);
        priminimai_button.setBackgroundColor(col);

        col = (!dark) ? ContextCompat.getColor(getApplicationContext(), R.color.selectedItemColor) : ContextCompat.getColor(getApplicationContext(), R.color.selectedItemColor1);
        view.setBackgroundColor(col);
    }
    /*--------------------------------------------------------------------------------------*/




    /*------------------------- Išeiti iš nustatymų meniu ----------------------------------*/
    /**Funkcija grįžti į pradinį ekraną**/
    public void returnToMenu(View view) {
        exitActivity();
    }

    @Override
    public void onBackPressed() {
        exitActivity();
    }

    void exitActivity() {
        Funkcijos.setList(mPrefs, "pazymeti", pazymetiMokiniai);
        mPrefs.edit().putBoolean("switched", switched).apply();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("keite_nustatymus", keite_nustatymus);
        returnIntent.putExtra("needs_restart", mPrefs.getBoolean("darkTheme", false) != mPrefs.getBoolean("mainActivityDarkTheme", false));
        setResult(RESULT_OK, returnIntent);

        finish();
    }
    /*--------------------------------------------------------------------------------------*/




    /*----------------- Pakeisti priminimų ir kt. nustatymus -------------------------------*/
    void changeSettingColor(boolean ijungta, int ID) {
        TextView busena = findViewById(ID);
        String text;
        int col;
        boolean dark = mPrefs.getBoolean("darkTheme", false);

        if(ijungta) {
            text = "Įjungta";
            col = (!dark) ? ContextCompat.getColor(getApplicationContext(), R.color.greenText) : ContextCompat.getColor(getApplicationContext(), R.color.greenText1);
        } else {
            text = "Išjungta";
            col = (!dark) ? ContextCompat.getColor(getApplicationContext(), R.color.redText) : ContextCompat.getColor(getApplicationContext(), R.color.redText1);
        }

        busena.setText(text);
        busena.setTextColor(col);
    }


    /*--------------------------- Priminimai ----------------------------*/
    /**Funkcija, įjungianti ar išjungianti priminimus*/
    public void pakeistiPriminimuNustatymus(View view) {
        keite_nustatymus = true;
        priminimai = mPrefs.getBoolean("priminimai", false);
        if(priminimai) {
            atsauktiPriminimus();
        } else {
            nustatytiPriminimus();
        }
    }

    /**Funkcija, pritaikanti priminimų nustatymo tekstą padarytam pasirinkimui*/
    private void setPriminimaiTextProperties(boolean priminimai) {
        TextView priminimaiInfo = findViewById(R.id.priminimai_info);
        LinearLayout papildomiNustatymai = findViewById(R.id.papildomiNustatymai);

        if(priminimai) {
            papildomiNustatymai.setVisibility(View.VISIBLE);
            priminimaiInfo.setVisibility(View.GONE);
        } else {
            papildomiNustatymai.setVisibility(View.GONE);
            priminimaiInfo.setVisibility(View.VISIBLE);
            priminimaiInfo.setText(getString(R.string.isjungus_pranesimus));
        }

        changeSettingColor(priminimai, R.id.priminimuBusena);
    }

    /**Funkcija nustatyti visus priminimus apie pamokas paspaudus mygtuk1*/
    public void nustatytiPriminimus() {
        if(Funkcijos.nustatytiPriminimus(getSharedPreferences("label", 0), getApplicationContext()) == 1) {
            Toast.makeText(getApplicationContext(), R.string.nepavyko_atnaujinti_priminimu, Toast.LENGTH_LONG).show();
            finish();
        } else {
            setPriminimaiTextProperties(true);
        }
    }

    /**Funkcija ištrinti visus priminimus apie pamokas paspaudus mygtuką*/
    public void atsauktiPriminimus() {
        Funkcijos.atsauktiPriminimus(getApplicationContext(), mPrefs);
        setPriminimaiTextProperties(false);

        Funkcijos.removeNotification(this, 3);
    }
    /*-------------------------------------------------------------------*/


    /*--------------------------- Vibracija ----------------------------*/
    /**Funkcija, įjungianti ar išjungianti vibraciją*/
    public void pakeistiVibracijosBusena(View view) {
        keite_nustatymus = true;
        vibracija = !mPrefs.getBoolean("vibracija", true);
        mPrefs.edit().putBoolean("vibracija", vibracija).apply();
        changeSettingColor(vibracija, R.id.vibracijosBusena);
    }
    /*-------------------------------------------------------------------*/


    /*--------------------------- Informacija ----------------------------*/
    /**Funkcija, pakeičianti, kokią informaciją rodyti pranešimuose*/
    public void pakeistiInformacijosBusena(View view) {
        keite_nustatymus = true;
        int busena = mPrefs.getInt("informacija", 0);
        switch(busena) {
            case 0:
                busena = 1;
                break;
            case 1:
                busena = 0;
                break;
        }
        findViewById(R.id.informacijos_paaiskinimas).setVisibility(View.VISIBLE);
        mPrefs.edit().putInt("informacija", busena).apply();
        setInformacijaTextProperties(busena);
    }

    /**Funkcija, pritaikanti rodomos informacijos nustatymo tekstą padarytam pasirinkimui*/
    void setInformacijaTextProperties(int busena) {
        TextView informacijosBusena = findViewById(R.id.informacijosBusena);
        switch(busena) {
            case 0:
                informacijosBusena.setText(R.string.smulki_informacija);
                break;
            case 1:
                informacijosBusena.setText(R.string.pabaigos_laikas);
                break;
        }
    }
    /*-------------------------------------------------------------------*/


    /*--------------------------- Panaikinimas ----------------------------*/
    /**Funkcija, įjungianti ar išjungianti pranešimų panaikinimą nubraukiant*/
    public void pakeistiPanaikinimoNustatymus(View view) {
        keite_nustatymus = true;
        panaikinimai = !mPrefs.getBoolean("panaikinimai", false);
        mPrefs.edit().putBoolean("panaikinimai", panaikinimai).apply();
        setPanaikinimaiTextProperties();
    }

    /**Funkcija, pritaikanti pranešimų panaikinimo nustatymo tekstą padarytam pasirinkimui*/
    void setPanaikinimaiTextProperties() {
        TextView panaikinimoInfo = findViewById(R.id.panaikinimo_info);
        panaikinimoInfo.setText(panaikinimai ? getString(R.string.panaikinimai_ijungti) : getString(R.string.panaikinimai_isjungti));
        changeSettingColor(panaikinimai, R.id.panaikinimoBusena);
    }
    /*-------------------------------------------------------------------*/


    /*--------------------------- Nukirpimas ----------------------------*/
    /**Funkcija, pakeičianti pirmo žodžio nuo pavadinimo nukirpimo nustatymus*/
    public void pakeistiNukirpimoNustatymus(View view) {
        keite_nustatymus = true;
        boolean nukirpimas = !mPrefs.getBoolean("nukirpimas", false);
        mPrefs.edit().putBoolean("nukirpimas", nukirpimas).apply();
        findViewById(R.id.trimWarning).setVisibility(View.VISIBLE);
        setNukirpimoTextProperties();
    }

    /**Funkcija, pritaikanti pavadinimo apkirpimo tekstą padarytam pasirinkimui*/
    void setNukirpimoTextProperties() {
        TextView trimInfo = findViewById(R.id.trimInfo);
        boolean nukirpimas = mPrefs.getBoolean("nukirpimas", false);
        changeSettingColor(nukirpimas, R.id.trimBusena);
        trimInfo.setText(nukirpimas ? getString(R.string.nukirpimas_on) : getString(R.string.nukirpimas_isjungtas));
    }
    /*-------------------------------------------------------------------*/


    /*--------------------------- AutoUpdate ----------------------------*/
    public void pakeistiAutoUpdateNustatymus(View view) {
        keite_nustatymus = true;
        boolean autoUpdate = !mPrefs.getBoolean("autoUpdate", true);
        mPrefs.edit().putBoolean("autoUpdate", autoUpdate).apply();
        changeSettingColor(autoUpdate, R.id.autoUpdateBusena);
    }
    /*-------------------------------------------------------------------*/

    /*--------------------------- AutoUpdate ----------------------------*/
    public void pakeistiVersionUpdateNustatymus(View view) {
        keite_nustatymus = true;
        boolean versionUpdate = !mPrefs.getBoolean("versionUpdate", true);
        mPrefs.edit().putBoolean("versionUpdate", versionUpdate).apply();
        changeSettingColor(versionUpdate, R.id.versionUpdateBusena);
    }
    /*-------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------*/




    /*--------------------------------Sukurti mokinių sąrašus --------------------------------*/
    /**Funkcija nustatyti pažymėų mokinių sąrašą*/
    void nustatytiPazymetuSarasa() {
        RecyclerView recyclerView;

        recyclerView = findViewById(R.id.pazymetiVardai);
        pazAdapter = new MokiniaiAdapter(pazymetiMokiniai);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(pazAdapter);

        //Uzkrauname pazymetu mokiniu sarasa is atminties (SharedPreferences)
        pazymetiMokiniai.clear();
        pazymetiMokiniai.addAll(Funkcijos.getList(mPrefs, "pazymeti"));
        if(pazymetiMokiniai.size() == 0) {
            Mokinys mok = new Mokinys(getString(R.string.palaikykite_paspaude), "sample");
            pazymetiMokiniai.add(mok);
        }
        pazAdapter.notifyDataSetChanged();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                keite_nustatymus = true;
                nustatytiLink(position, true);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if(pazymetiMokiniai.size() > position && position >= 0) {
                    pazymetiMokiniai.remove(position);
                    pazAdapter.notifyDataSetChanged();
                }
            }
        }));
    }

    /**funkcija sukurti vardu sarasui, jo nustatymams ir elemento (vardo) paspaudimui**/
    void nustatytiSarasa() {
        RecyclerView recyclerView;

        recyclerView = findViewById(R.id.nameList);
        mAdapter = new MokiniaiAdapter(rodomiMokiniai);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        Mokinys mok = new Mokinys(getString(R.string.press_load_names), "sample");
        rodomiMokiniai.add(mok);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                nustatytiLink(position, false);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if(pazymetiMokiniai.size() == 1 && pazymetiMokiniai.get(0).getNuoroda().equals("sample"))
                    pazymetiMokiniai.clear();
                pazymetiMokiniai.add(rodomiMokiniai.get(position));
                pazAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), getString(R.string.pazymetas_mokinys) + rodomiMokiniai.get(position).getVardas(), Toast.LENGTH_SHORT).show();
            }
        }));
    }
    /*----------------------------------------------------------------------------------------*/




    /*--------------------------Pakeisti I-II/III/IV klasių pasirinkimą------------------------*/
    /**Funkcija, pakeičianti pasirinkimą tarp I-II ir III-IV klasių*/
    public void pakeistiRodomusMokinius(View view) {
        switched = mPrefs.getBoolean("switched", false);
        switched = !switched;

        mPrefs.edit().putBoolean("switched", switched).apply();
        String nameString = mPrefs.getString("nameString", "NULL");
        if(nameString.equals("NULL"))
            Toast.makeText(getApplicationContext(), R.string.atnaujinkite_sarasa, Toast.LENGTH_LONG).show();
        else
            analyzeNames(mPrefs.getString("nameString", "NULL"));
        setMokiniaiTextProperties();
    }

    /**Funkcija, pritaikanti pasirinkimo tekstą padarytam klasių pasirinkimui*/
    void setMokiniaiTextProperties() {
        TextView rodomiMokiniai = findViewById(R.id.rodomiMokiniai);
        rodomiMokiniai.setText(mPrefs.getBoolean("switched", false) ? R.string.III_IV : R.string.I_II);
    }
    /*----------------------------------------------------------------------------------------*/




    /*----------------------- Veiksmai su rodomais mokiniais-----------------------------------*/
    /**Funkcija pakeisti rodomiems vardams, kai vartotojas iveda teksta i teksto laukeli*/
    public TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            rodomiMokiniai.clear();
            for(Mokinys m : mokiniai)
                if(m.getVardas().toLowerCase().contains(charSequence.toString().toLowerCase()))
                    rodomiMokiniai.add(m);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void afterTextChanged(Editable editable) { }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
    };

    /**funkcija pakeisti tvarkarascio nuorodai i pasirinkta is saraso**/
    void nustatytiLink(int position, boolean isPazymetu) {
        String link;
        String vardas;
        if(isPazymetu) {
            link = pazymetiMokiniai.get(position).getNuoroda();
            vardas = pazymetiMokiniai.get(position).getVardas();
        } else {
            link = rodomiMokiniai.get(position).getNuoroda();
            vardas = rodomiMokiniai.get(position).getVardas();
        }

        if(!link.equals("sample")) {
            keite_nustatymus = true;
            mPrefs.edit().putString("link", link).apply();
            mPrefs.edit().putString("pamokuLink", link).apply();
            mPrefs.edit().putString("pamokosAtnaujintos", Funkcijos.getLaikas()).apply();

            Funkcijos.getString(getApplicationContext(), mPrefs.getString("link", "NULL"), mPrefs, "pamokos"); //is anksto uzkrauname tvarkarasti
            Toast.makeText(getApplicationContext(), getString(R.string.set_link_for) + vardas, Toast.LENGTH_LONG).show();
            Funkcijos.atsauktiPriminimus(getApplicationContext(), mPrefs);
        }
    }
    /*----------------------------------------------------------------------------------------*/




    /*----------------------- Siųstis, analizuoti ir rodyti vardus ----------------------------*/
    /**funkcija tikrinti, ar reikia atnaujinti vardu sarasa**/
    public void parseNames(View view) {
        String nameString = mPrefs.getString("nameString", "NULL");
        if(nameString.equals("NULL")) { //jeigu nebuvo issaugotas vardu sarasas
            updateNameString();
        } else { //jeigu vardu sarasas jau buvo issaugotas
            updateNameListDialog();
        }
    }

    /**Dialogas paklausti, ar vartotojas nori atnaujinti vardu sarasa, ar naudoti sena**/
    void updateNameListDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);

        final TextView dialog_text = dialog.findViewById(R.id.dialog_text),
                positive_button = dialog.findViewById(R.id.dialog_positive_button),
                negative_button = dialog.findViewById(R.id.dialog_negative_button),
                neutral_button = dialog.findViewById(R.id.dialog_neutral_button);

        String text = getString(R.string.list_was_updated) + mPrefs.getString("namesUpdatedAt", "NULL") + getString(R.string.list_was_updated_2_dalis);

        dialog_text.setText(text);
        positive_button.setText(getString(R.string.download_new_list));
        negative_button.setText(getString(R.string.use_old_list));

        positive_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keite_nustatymus = true;
                updateNameString();
                dialog.dismiss();
            }
        });
        negative_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keite_nustatymus = true;
                analyzeNames(mPrefs.getString("nameString", "NULL"));
                dialog.dismiss();
            }
        });
        neutral_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**funkcija atnaujinti vardu sarasui**/
    void updateNameString() {
        Funkcijos.getString(getApplicationContext(), getString(R.string.main_link), mPrefs, "nameString");

        BroadcastReceiver message = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                analyzeNames(mPrefs.getString("nameString", "NULL"));
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(message, new IntentFilter("name_download_finished"));

        mPrefs.edit().putString("namesUpdatedAt", Funkcijos.getLaikas()).apply();
    }

    /**funkcija vardu saraso HTML tekstui suskirstyti i kintamuosius*/
    public void analyzeNames(String result) {
        inputName.setHint(R.string.iveskite_varda);
        String [] resultArr = result.split("\n", 0); //skaidome sakini visur, kur prasideda nauja eilute
        boolean moksleiviai = false; //ar siuo metu skaitomas sakinys priklauso "Moksleiviai" lenteles skilciai
        mokiniai.clear(); //panaikiname standartinius elementus sarase
        rodomiMokiniai.clear();

        for(String a : resultArr) {
            String nameInString = "", linkInString = "";
            if(moksleiviai && a.contains("<a h")) {
                nameInString = Funkcijos.findNameInString(a, false); //pazymime, kad sakinyje nurodoma mokinio name bei pavarde
                linkInString = Funkcijos.findLinkInString(a);
            } if(switched && a.contains("Moksleiviai"))
                moksleiviai = true; //pazymimie, kad prasidejo "Moksleiviai" lenteles skiltis
            else if(!switched && a.contains("Klasės"))
                moksleiviai = true;
            else if(!switched && a.contains("Mokytojai"))
                moksleiviai = false;

            if(!nameInString.equals("")) {
                Mokinys mok = new Mokinys(nameInString, linkInString);
                mokiniai.add(mok); //pridedame nuskaitytus mokinio duomenis i sarasa
                rodomiMokiniai.add(mok);
            }
        }

        mokiniai.remove(mokiniai.size()-1); //pasaliname paskutini - "mimosasoftware.com" elementa
        rodomiMokiniai.remove(rodomiMokiniai.size()-1);
        mAdapter.notifyDataSetChanged();

        issaugotiMokinius(mokiniai);
    }



    private void issaugotiMokinius(List <Mokinys> mokiniai) {
        Funkcijos.setList(mPrefs, "mokiniuSarasas", mokiniai);
        mPrefs.edit().putString("issaugotoSarasoTekstas", mPrefs.getString("nameString", "ERROR")).apply();
        mPrefs.edit().putBoolean("issaugotoSarasoSwitched", mPrefs.getBoolean("switched", false)).apply();
    }
    /*----------------------------------------------------------------------------------------*/
}
