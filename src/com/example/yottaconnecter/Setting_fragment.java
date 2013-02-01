package com.example.yottaconnecter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import com.example.yottaconnecter.R;



public class Setting_fragment extends DialogFragment implements OnClickListener{
	private static final int REQUEST_GALLERY = 0;
	private Button setting_button;
	private ImageView icon_view;
	private EditText name_text;
	private EditText profile_text;
	private Switch GPS_switch;

	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		WindowManager wm = (WindowManager)getActivity().getSystemService(getActivity().WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		Dialog sDialog = super.onCreateDialog(savedInstanceState);
		sDialog.setContentView(CreateContent());
		sDialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
		sDialog.getWindow().getAttributes().height = disp.getHeight();
		sDialog.getWindow().getAttributes().softInputMode = LayoutParams.SOFT_INPUT_STATE_HIDDEN;
		
		return sDialog;
	}
	
	private View CreateContent(){
		String path = "data/data/" + getActivity().getPackageName() + "/MyData/";  
		View v = (View)getActivity().getLayoutInflater().inflate(R.layout.setting_fragment, null);
		setting_button = (Button)v.findViewById(R.id.set_SettingButton);
		icon_view = (ImageView)v.findViewById(R.id.set_IconView);
		name_text = (EditText)v.findViewById(R.id.set_UserNameText);
		profile_text = (EditText)v.findViewById(R.id.set_ProfileText);
		GPS_switch = (Switch)v.findViewById(R.id.set_GPSswitch);
		
		setting_button.setOnClickListener(this);
		icon_view.setOnClickListener(this);
		
		name_text.setText(MainActivity.MyNode.getName());
		
		profile_text.setText(MainActivity.MyNode.getProfile());
		
		
		File srcFile = new File(path + "icon.jpg");
		try {
			FileInputStream fis = new FileInputStream(srcFile);
			Bitmap bitmap = BitmapFactory.decodeStream(fis);
			
			icon_view.setImageBitmap(bitmap);
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		return v;
	}

	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		if(v == setting_button){
			makeXML();
			getActivity().getFragmentManager().beginTransaction().remove(this).commit();
		}else if(v == icon_view){
			Intent intent = new Intent(Intent.ACTION_PICK);
	        intent.setType("image/*");
	        startActivityForResult(intent, REQUEST_GALLERY);
		}
	}
	
    public void makeXML(){
    	DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
    	 try {
    	     DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
    	     Document document = dbuilder.newDocument();
    	     Element root = document.createElement("members");
    	 
    	     //要素を作成
    	     Element name = document.createElement("name");
    	     Text text = document.createTextNode(name_text.getText().toString());
    	     name.appendChild(text);
    	     
    	     Element profile = document.createElement("profile");
    	     text = document.createTextNode(profile_text.getText().toString());
    	     profile.appendChild(text);
    	     
    	     
    	     //各要素を親ノードへ追加
    	     root.appendChild(name);
    	     root.appendChild(profile);
    	     document.appendChild(root);
    	 
    	 
    	     TransformerFactory tffactory = TransformerFactory.newInstance();
    	     Transformer transformer = tffactory.newTransformer(); //XML保存先ディレクトリ
    	     String path = "data/data/" + getActivity().getPackageName() + "/MyData/";  
    	     File dir = new File(path);
    	     if(!dir.exists()){
    	         dir.mkdir();
    	     }
    	     File file = new File(path + "UserData.xml");
    	     if(!file.exists()){
    	         file.createNewFile();
    	         
    	     }
    	     transformer.transform(new DOMSource(document), new StreamResult(file));
    	     FileOutputStream fos = new FileOutputStream(path + "icon.jpg");
    	     BitmapDrawable bd = (BitmapDrawable)icon_view.getDrawable();
    	     Bitmap bitmap =  bd.getBitmap();
    	     bitmap.compress(CompressFormat.JPEG, 100, fos);
    	     fos.flush();
    	     fos.close();
    	       
    	     
    	 } catch (ParserConfigurationException e) {
    	     // TODO Auto-generated catch block
    	     e.printStackTrace();
    	 }catch (TransformerConfigurationException e) {
    	     // TODO Auto-generated catch block
    	     e.printStackTrace();
    	 } catch (TransformerException e) {
    	     // TODO Auto-generated catch block
    	     e.printStackTrace();
    	 } catch (IOException e) {
    	     // TODO 自動生成された catch ブロック
    	     e.printStackTrace();
    	 }
    	 
    	 MainActivity.MyNode.setName(name_text.getText().toString());
    	 MainActivity.MyNode.setProfile(profile_text.getText().toString());
    }
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO 自動生成されたメソッド・スタブ
    	super.onActivityResult(requestCode, resultCode, data); 
    	getActivity();
		if(resultCode == Activity.RESULT_OK){
    		Bitmap bitmap = null;
    		try {
    			InputStream is =  getActivity().getContentResolver().openInputStream(data.getData());
    			
    			bitmap = BitmapFactory.decodeStream(is);
    			icon_view.setImageBitmap(bitmap);
    			is.close();
    			 
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
}
