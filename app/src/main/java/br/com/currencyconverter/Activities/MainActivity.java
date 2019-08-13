package br.com.currencyconverter.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import br.com.currencyconverter.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    
    @BindView(R.id.edt_quantia_em_dolar)
    EditText edt_quantia_em_dolar;
    @BindView(R.id.edt_valor_dolar_turismo)
    EditText edt_valor_dolar_turismo;
    @BindView(R.id.edt_valor_taxa_impostos)
    EditText edt_valor_taxa_impostos;
    @BindView(R.id.radio_group_iof)
    RadioGroup radio_group_iof;
    @BindView(R.id.btCalcular)
    Button btCalcular;
    
    //Dialog-----------
    private Dialog myDialogMainActivity;
    //-------------
    
    private boolean iofCartao = true;
    
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        
        sharedPreferences = getSharedPreferences("SHARED_PREFERENCES", Context.MODE_PRIVATE);
        
        getSharedPreferences();
    
        edt_quantia_em_dolar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edt_quantia_em_dolar.setBackground(getDrawable(R.drawable.background_edit_text_with_border_on));
                } else {
                    edt_quantia_em_dolar.setBackground(getDrawable(R.drawable.background_edit_text_with_border_off));
                }
            }
        });
    
        edt_valor_dolar_turismo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edt_valor_dolar_turismo.setBackground(getDrawable(R.drawable.background_edit_text_with_border_on));
                } else {
                    edt_valor_dolar_turismo.setBackground(getDrawable(R.drawable.background_edit_text_with_border_off));
                }
            }
        });
    
        edt_valor_taxa_impostos.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edt_valor_taxa_impostos.setBackground(getDrawable(R.drawable.background_edit_text_with_border_on));
                } else {
                    edt_valor_taxa_impostos.setBackground(getDrawable(R.drawable.background_edit_text_with_border_off));
                }
            }
        });
    
        // This overrides the radiogroup onCheckListener
        radio_group_iof.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if(checkedId == R.id.rb_iof_especie){
                    iofCartao = false;
                } else if(checkedId == R.id.rb_iof_cartao){
                    iofCartao = true;
                }
            }
        });
        
        btCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificaCamposNecessarios()){
                    final ImageView ivClose;
                    final TextView tvResultado;
    
                    myDialogMainActivity = new Dialog(MainActivity.this);
                    myDialogMainActivity.setContentView(R.layout.popup_result_dialog);
                    myDialogMainActivity.getWindow().getAttributes().windowAnimations = R.style.DialogStyleAnimationScale;
                    ivClose = myDialogMainActivity.findViewById(R.id.ivClose);
                    tvResultado = myDialogMainActivity.findViewById(R.id.tvResultado);
                    
                    tvResultado.setText("R$ " + String.format("%.2f", realizaCalculo()));
    
                    ivClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myDialogMainActivity.dismiss();
                        }
                    });
    
                    myDialogMainActivity.show();
                    
                    setSharedPreferences();
                }
            }
        });
        
    }
    
    private double realizaCalculo(){
        double valorTotal = 0.0;
        double quantiaEmDolar = Double.parseDouble(edt_quantia_em_dolar.getText().toString());
        double valorDolaTurismo = Double.parseDouble(edt_valor_dolar_turismo.getText().toString());
        double valorTaxaImpostos = Double.parseDouble(edt_valor_taxa_impostos.getText().toString());
        double taxaIof = ((iofCartao) ? 6.38 : 1.1);
        
        valorTotal = quantiaEmDolar + ((quantiaEmDolar * valorTaxaImpostos)/100);
        
        valorTotal = valorTotal * valorDolaTurismo;
        
        valorTotal = valorTotal + ((valorTotal * taxaIof)/100);
        
        return valorTotal;
    }
    
    private boolean verificaCamposNecessarios(){
        boolean podeProsseguir = true;
        
        if(edt_quantia_em_dolar.getText().toString().isEmpty()){
            edt_quantia_em_dolar.setError("Este campo é obrigatório");
            podeProsseguir = false;
        }
    
        if(edt_valor_dolar_turismo.getText().toString().isEmpty()){
            edt_valor_dolar_turismo.setError("Este campo é obrigatório");
            podeProsseguir = false;
        }
    
        if(edt_valor_taxa_impostos.getText().toString().isEmpty()){
            edt_valor_taxa_impostos.setText("0.00");
        }
        
        return podeProsseguir;
    }
    
    public void getSharedPreferences(){
        edt_quantia_em_dolar.setText(sharedPreferences.getString("quantia_em_dolar", ""));
        edt_valor_dolar_turismo.setText(sharedPreferences.getString("valor_dolar_turismo", ""));
        edt_valor_taxa_impostos.setText(sharedPreferences.getString("valor_taxa_impostos", ""));
        iofCartao = sharedPreferences.getBoolean("iof_cartao", true);
        if(iofCartao){
            RadioButton checkedRadioButton = (RadioButton)radio_group_iof.findViewById(R.id.rb_iof_cartao);
            checkedRadioButton.setChecked(true);
        } else {
            RadioButton checkedRadioButton = (RadioButton)radio_group_iof.findViewById(R.id.rb_iof_especie);
            checkedRadioButton.setChecked(true);
        }
    }
    
    public void setSharedPreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("quantia_em_dolar", edt_quantia_em_dolar.getText().toString());
        editor.putString("valor_dolar_turismo", edt_valor_dolar_turismo.getText().toString());
        editor.putString("valor_taxa_impostos", edt_valor_taxa_impostos.getText().toString());
        editor.putBoolean("iof_cartao", iofCartao);
        editor.apply();
    }
    
}
