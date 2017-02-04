package com.lucrus.main.components;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lucrus.main.R;
import com.lucrus.main.Utility;
import com.lucrus.main.activities.PopupListActivity;
import com.lucrus.main.fontawesome.DrawableAwesome;
import com.lucrus.main.validation.Validator;
import com.lucrus.main.validation.ValidatorFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by lucrus on 12/10/16.
 */

public class EditText extends RelativeLayout {
    private android.widget.EditText et;
    private CheckBox cb;
    private LinearLayout llRadio;
    private List<TextView> mRadios;
    private String mRadioValue;
    private TextView tvLabel, tvError;
    private int okColor = Color.parseColor("#3c763d"), errorColor = Color.parseColor("#843534"), neutralColor = Color.GRAY, focusColor = Color.parseColor("#FAB900"); //Color.YELLOW;
    private Drawable radioChecked, radioUnchecked;
    private Drawable okDrawable, errorDrawable;
    private GradientDrawable mShape;
    private Button bPezz;
    private List<Validator> mValidators;
    private Boolean mValid;
    private List<String> mRelatedFields;
    private boolean mCheck, mList, mRadio;
    private TipoDato mTipoDato;
    private ArrayList<PopupListActivity.ListItem> mListItems = new ArrayList<>();
    private int mListRequestCode;

    private static final Random RANDOM = new Random();

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mList) {
                showDropdownList();
            } else {
                handleOn();
            }
        }
    };

    public EditText(Context context) {
        super(context);
        //setOrientation(VERTICAL);
        init(context);
    }

    public EditText(Context context, boolean check) {
        super(context);
        mCheck = check;
        init(context);
    }

    public EditText(Context context, TipoDato tipoDato) {
        super(context);
        mTipoDato = tipoDato;
        if (mTipoDato == TipoDato.Booleano) {
            mCheck = true;
        } else if (mTipoDato == TipoDato.Lista) {
            mList = true;
        } else if (mTipoDato == TipoDato.Radio) {
            mRadio = true;
            mRadios = new ArrayList<>();
            radioChecked = new DrawableAwesome.Builder(context, R.string.fa_check_circle_o).setFakeBold(false).setSize(20).build();
            radioUnchecked = new DrawableAwesome.Builder(context, R.string.fa_circle_o).setFakeBold(false).setSize(20).build();
        }
        init(context);
        setType(mTipoDato);
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setOrientation(VERTICAL);
        init(context);
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //setOrientation(VERTICAL);
        init(context);
    }

    private void init(Context context) {
        //LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //lp.weight
        mValid = null;
        mValidators = new ArrayList<>();
        mRelatedFields = new ArrayList<>();
        okDrawable = new DrawableAwesome.Builder(context, R.string.fa_check).setFakeBold(false).setSize(20).setColor(okColor).build();
        errorDrawable = new DrawableAwesome.Builder(context, R.string.fa_remove).setFakeBold(false).setSize(20).setColor(errorColor).build();

        View main;
        if (mRadio) {
            llRadio = new LinearLayout(context);
            llRadio.setOrientation(LinearLayout.VERTICAL);
            main = llRadio;
        } else if (mCheck) {
            cb = new CheckBox(context);
            main = cb;
        } else {
            et = new android.widget.EditText(context);
            main = et;
            et.setClickable(true);
            et.setPadding(6, 2, 6, 2);
            //int[] colors = new int[]{Color.parseColor("#c3c3c3"), Color.parseColor("#FFFFFF")};
            //GradientDrawable shape = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
            mShape = new GradientDrawable();
            mShape.setShape(GradientDrawable.RECTANGLE);
            mShape.setCornerRadius(5);
            mShape.setStroke(1, neutralColor);
            mShape.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                et.setBackground(mShape);
            } else {
                et.setBackgroundDrawable(mShape);
            }

            if (mList) {
                et.setEnabled(false);
                et.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        new DrawableAwesome.Builder(context, R.string.fa_ellipsis_h).setFakeBold(false).setSize(20).build()
                        , null);
            }
        }
        tvError = new TextView(context);
        tvError.setId(RANDOM.nextInt());
        tvError.setVisibility(GONE);
        tvError.setClickable(false);
        tvError.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvLabel = new TextView(context);
        tvLabel.setId(RANDOM.nextInt());
        tvLabel.setClickable(false);
        addView(tvLabel);
        addView(tvError);
        addView(main);

        //et.setEnabled(false);
        ((LayoutParams) tvLabel.getLayoutParams()).leftMargin = 2;
        ((LayoutParams) tvLabel.getLayoutParams()).width = LayoutParams.MATCH_PARENT;
        ((LayoutParams) tvLabel.getLayoutParams()).height = LayoutParams.WRAP_CONTENT;
        ((LayoutParams) tvLabel.getLayoutParams()).addRule(ALIGN_PARENT_TOP);// = 2;
        ((LayoutParams) main.getLayoutParams()).addRule(BELOW, tvError.getId());
        ((LayoutParams) main.getLayoutParams()).leftMargin = 2;
        ((LayoutParams) main.getLayoutParams()).rightMargin = 2;
        ((LayoutParams) main.getLayoutParams()).width = LayoutParams.MATCH_PARENT;
        ((LayoutParams) main.getLayoutParams()).height = LayoutParams.WRAP_CONTENT;
        ((LayoutParams) tvError.getLayoutParams()).leftMargin = 2;
        ((LayoutParams) tvError.getLayoutParams()).addRule(BELOW, tvLabel.getId());

        setClickable(true);

        if (mRadio) {
        } else if (mCheck) {
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    validate(false);
                }
            });
        } else {
            bPezz = new Button(context);
            bPezz.setBackgroundColor(Color.parseColor("#00000000"));
            addView(bPezz);
            ((LayoutParams) bPezz.getLayoutParams()).width = LayoutParams.MATCH_PARENT;
            ((LayoutParams) bPezz.getLayoutParams()).height = LayoutParams.MATCH_PARENT;
            bPezz.setOnClickListener(mClickListener);

            et.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (et.getInputType() == InputType.TYPE_DATETIME_VARIATION_DATE) {
                        return true;
                    }
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        switch (event.getKeyCode()) {
                            case KeyEvent.KEYCODE_ENTER:
                            case KeyEvent.KEYCODE_DPAD_RIGHT:
                            case KeyEvent.KEYCODE_DPAD_DOWN:
                                Log.d("EDIT-TEXT", "NEXT -> CurrentId: " + getId() + " movingOn: " + getNextFocusRightId());
                                try {
                                    View rv = getRootView();
                                    View next = rv.findViewById(et.getNextFocusRightId());
                                    ViewParent vp = next.getParent();
                                    EditText me = (EditText) vp;
                                    me.handleOn();
                                } catch (Exception e) {
                                }
                                return true;
                            case KeyEvent.KEYCODE_DPAD_LEFT:
                            case KeyEvent.KEYCODE_DPAD_UP:
                                Log.d("EDIT-TEXT", "PREV -> CurrentId: " + getId() + " movingOn: " + getNextFocusLeftId());
                                try {
                                    View rv = getRootView();
                                    View next = rv.findViewById(et.getNextFocusLeftId());
                                    ViewParent vp = next.getParent();
                                    EditText me = (EditText) vp;
                                    me.handleOn();
                                } catch (Exception e) {
                                }
                                return true;
                        }

                    }
                    int kc = event.getKeyCode();
                    return kc == KeyEvent.KEYCODE_ENTER || kc == KeyEvent.KEYCODE_DPAD_RIGHT || kc == KeyEvent.KEYCODE_DPAD_DOWN ||
                            kc == KeyEvent.KEYCODE_DPAD_LEFT || kc == KeyEvent.KEYCODE_DPAD_UP || et.getInputType() == InputType.TYPE_DATETIME_VARIATION_DATE;
                }
            });

            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    validate(false);
                }
            });

            setupButton();
        }
    }

    private void showDropdownList() {
        Intent i = new Intent(getContext(), PopupListActivity.class);
        i.putParcelableArrayListExtra(PopupListActivity.ITEMS_NAME, mListItems);
        try {
            ((Activity) getContext()).startActivityForResult(i, mListRequestCode);
        } catch (Exception e) {
            Utility.log(getContext(), e);
        }
    }

    private void setupButton() {
        if (mCheck || mRadio) return;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int h = getHeight();
                        int oldH = bPezz.getHeight();
                        bPezz.setHeight(h);
                    }
                });
            }
        }, 300);
    }

    public void handleOn() {
        if (!isEnabled()) return;
        if (mCheck) {
            cb.requestFocus();
        } else {
            resetOthers();
            if (mRadio) {
                if (mRadios.size() > 0) {
                    mRadios.get(0).requestFocus();
                }
            } else {
                et.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, 0);
                mShape.setColor(focusColor);
                bPezz.setVisibility(GONE);
                if (et.getInputType() == InputType.TYPE_DATETIME_VARIATION_DATE) {
                    setDate(et);
                }
                if (mList) {
                    showDropdownList();
                }
            }
        }
    }

    public void setDate(final View v) {
        final DatePicker dp = new DatePicker(getContext());
        dp.setCalendarViewShown(false);
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setView(dp)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String data = String.format("%02d/%02d/%04d", dp.getDayOfMonth(), dp.getMonth() + 1, dp.getYear());
                        et.setText(data);
                        mShape.setColor(Color.parseColor("#00000000"));
                        int idNext = et.getNextFocusRightId();
                        if (idNext <= 0) {
                            idNext = et.getNextFocusLeftId();
                        }
                        try {
                            View rv = getRootView();
                            View next = rv.findViewById(idNext);
                            ViewParent vp = next.getParent();
                            EditText me = (EditText) vp;
                            me.handleOn();
                        } catch (Exception e) {
                        }
                    }
                }).show();
    }

    /*
    private void resetOthers0(ViewGroup mContainer) {
        if (mContainer == null) {
            return;
        }
        final int mCount = mContainer.getChildCount();
        for (int i = 0; i < mCount; ++i) {
            final View mChild = mContainer.getChildAt(i);
            if (mChild instanceof EditText && mChild!=this) {
                ((EditText)mChild).reset();
            } else if (mChild instanceof ViewGroup) {
                // Recursively attempt another ViewGroup.
                resetOthers0((ViewGroup) mChild);
            }
        }
    }
    */
    private void resetOthers() {
        List<View> all = findAll((ViewGroup) getRootView());
        for (View e : all) {
            if (e instanceof EditText && e != this) {
                ((EditText) e).reset();
            }
        }
    }

    private void reset() {
        if (mCheck || mRadio) return;
        Log.d("EDIT-TEXT", et.getText().toString());
        mShape.setColor(Color.parseColor("#00000000"));
        bPezz.setVisibility(VISIBLE);
        et.requestLayout();
    }

    private List<View> findAll(ViewGroup vg) {
        ArrayList<View> res = new ArrayList<>();
        if (vg == null) {
            return res;
        }
        final int mCount = vg.getChildCount();
        for (int i = 0; i < mCount; ++i) {
            final View mChild = vg.getChildAt(i);
            if (mChild instanceof EditText) {
                res.add(mChild);
            } else if (mChild instanceof ViewGroup) {
                res.addAll(findAll((ViewGroup) mChild));
            }
        }
        return res;
    }

    private List<View> findAll() {
        return findAll((ViewGroup) getRootView());
    }


    public void setLabel(String label) {
        if (tvLabel != null) {
            tvLabel.setText(label);
        }
    }

    public String getLabel() {
        if (tvLabel != null) {
            return tvLabel.getText().toString();
        }
        return "";
    }

    public Editable getText() {
        if (mRadio) {
            if (mRadioValue == null) mRadioValue = "";
            return new SpannableStringBuilder(mRadioValue);
        } else if (mCheck) {
            return new SpannableStringBuilder("" + cb.isChecked());
        } else {
            return et.getText();
        }
    }

    public void setText(CharSequence text) {
        if (mCheck) return;
        if (mRadio) {
            if (mRadios == null) return;
            for (TextView t : mRadios) {
                if (t.getTag().toString().equalsIgnoreCase(text.toString())) {
                    t.setCompoundDrawablesWithIntrinsicBounds(radioChecked, null, null, null);
                    mRadioValue = text.toString();
                } else {
                    t.setCompoundDrawablesWithIntrinsicBounds(radioUnchecked, null, null, null);
                }
            }
        } else {
            et.setText(text);
        }
    }

    public void setChecked(boolean checked) {
        if (cb != null) {
            cb.setChecked(checked);
        }
    }

    public boolean isChecked() {
        if (cb != null) {
            return cb.isChecked();
        }
        return false;
    }

    @Override
    public int getId() {
        if (mCheck) {
            if (cb == null) {
                return 0;
            }
            return cb.getId();
        } else {
            if (et == null) {
                return 0;
            }
            return et.getId();
        }
    }

    @Override
    public void setId(int id) {
        if (mRadio) {
            //null per il momento credo che non mi serve nessun id
        } else if (mCheck) {
            cb.setId(id);
        } else {
            et.setId(id);
        }
    }

    @Override
    public void setNextFocusForwardId(int id) {
        if (mRadio) {

        } else if (mCheck) {
            cb.setNextFocusForwardId(id);
        } else {
            et.setNextFocusForwardId(id);
        }
    }

    @Override
    public void setNextFocusDownId(int id) {
        if (mRadio) {

        } else if (mCheck) {
            cb.setNextFocusDownId(id);
        } else {
            et.setNextFocusDownId(id);
        }
    }

    @Override
    public void setNextFocusRightId(int id) {
        if (mRadio) {

        } else if (mCheck) {
            cb.setNextFocusRightId(id);
        } else {
            et.setNextFocusRightId(id);
        }
    }

    @Override
    public void setNextFocusLeftId(int id) {
        if (mRadio) {

        } else if (mCheck) {
            cb.setNextFocusLeftId(id);
        } else {
            et.setNextFocusLeftId(id);
        }
    }

    @Override
    public void setNextFocusUpId(int id) {
        if (mRadio) {

        } else if (mCheck) {
            cb.setNextFocusUpId(id);
        } else {
            et.setNextFocusUpId(id);
        }
    }

    @Override
    public int getNextFocusForwardId() {
        if (mRadio) {
            return 0;
        } else if (mCheck) {
            if (cb == null) return 0;
            return cb.getNextFocusForwardId();
        } else {
            if (et == null) return 0;
            return et.getNextFocusForwardId();
        }
    }

    @Override
    public int getNextFocusDownId() {
        if (mRadio) {
            return 0;
        } else if (mCheck) {
            if (cb == null) return 0;
            return cb.getNextFocusDownId();
        } else {
            if (et == null) return 0;
            return et.getNextFocusDownId();
        }
    }

    @Override
    public int getNextFocusRightId() {
        if (mRadio) {
            return 0;
        } else if (mCheck) {
            if (cb == null) return 0;
            return cb.getNextFocusRightId();
        } else {
            if (et == null) return 0;
            return et.getNextFocusRightId();
        }
    }

    @Override
    public int getNextFocusLeftId() {
        if (mRadio) {
            return 0;
        } else if (mCheck) {
            if (cb == null) return 0;
            return cb.getNextFocusLeftId();
        } else {
            if (et == null) return 0;
            return et.getNextFocusLeftId();
        }
    }

    @Override
    public int getNextFocusUpId() {
        if (mRadio) {
            return 0;
        } else if (mCheck) {
            if (cb == null) return 0;
            return cb.getNextFocusUpId();
        } else {
            if (et == null) return 0;
            return et.getNextFocusUpId();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (mCheck) {
            if (cb != null) {
                cb.setEnabled(enabled);
            }
        } else {
            if (et != null) {
                et.setEnabled(enabled);
            }
        }
    }

    public void setLines(int lines) {
        if (et != null) {
            et.setLines(lines);
        }
    }

    public void addTextChangedListener(TextWatcher tw) {
        if (et != null) {
            et.addTextChangedListener(tw);
        }
    }

    public void setType(TipoDato td) {
        mTipoDato = td;
        switch (td) {
            case Numerico:
                setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case Booleano:
                break;
            case Lista:
            case Radio:
                break;
            case Data:
                et.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
                break;
            case Testo:
            default:
                et.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
    }

    public void setInputType(int inputType) {
        if (et != null) {
            et.setInputType(inputType);
            if (et.getInputType() == InputType.TYPE_DATETIME_VARIATION_DATE) {
                et.setClickable(true);
                et.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setDate(et);
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    et.setShowSoftInputOnFocus(false);
                }
            }
        }
        //if(inputType==InputType.TYPE_DATETIME_VARIATION_DATE){
        //    et.setEnabled(false);
        //}
    }

    public void setValidation(String validation) {
        mRelatedFields.clear();
        if (validation == null || validation.trim().length() == 0) return;
        try {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> val = new Gson().fromJson(validation, type);
            for (String k : val.keySet()) {
                Validator v = ValidatorFactory.getValidator(k, val.get(k));
                if (v.getRelatedFieldKey() != null) {
                    mRelatedFields.add(v.getRelatedFieldKey());
                }
                mValidators.add(v);
            }
        } catch (Exception e) {
        }
    }


    public void validate(boolean related) {
        String ee = getText().toString().trim();
        for (Validator v : mValidators) {
            String key = v.getRelatedFieldKey();
            if (key != null) {
                List<View> all = findAll();
                v.setRelatedFieldValue("");
                for (View e : all) {
                    String fKey = "" + e.getTag(R.integer.key);

                    if (!related && e instanceof EditText) {
                        EditText eee = (EditText) e;

                        if (eee.mRelatedFields.contains(getTag(R.integer.key))) {
                            eee.validate(true);
                        }
                    }

                    if (key.equalsIgnoreCase(fKey)) {
                        if (e instanceof EditText) {
                            v.setRelatedFieldValue(((EditText) e).getText() + "");
                        }
                        //else if(e instanceof CheckBox){
                        //    v.setRelatedFieldValue(((CheckBox)e).isChecked()+"");
                        //}
                        //break;
                    }
                }
            }
            boolean valid = v.validate(ee);
            if (valid) {
                mValid = true;
                tvError.setText("");
                tvError.setVisibility(GONE);
                tvLabel.setTextColor(okColor);
                if (!mCheck && !mRadio && !mList) {
                    mShape.setStroke(1, okColor);
                    et.setCompoundDrawablesWithIntrinsicBounds(null, null, okDrawable, null);
                }
                if (mList) {
                    mShape.setStroke(1, okColor);
                }
            } else {
                tvError.setText(v.errorMessage());
                tvError.setVisibility(VISIBLE);
                tvError.setTextColor(errorColor);
                tvLabel.setTextColor(errorColor);
                if (!mCheck && !mRadio && !mCheck) {
                    mShape.setStroke(1, errorColor);
                    et.setCompoundDrawablesWithIntrinsicBounds(null, null, errorDrawable, null);
                }
                if (mList) {
                    mShape.setStroke(1, errorColor);
                }
                mValid = false;
                break;
            }
            setupButton();
        }
    }

    public boolean isCheck() {
        return mCheck;
    }

    public Boolean isValid() {
        return mValid;
    }

    public void onListActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mListRequestCode && resultCode == Activity.RESULT_OK) {
            PopupListActivity.ListItem li = data.getParcelableExtra("data");
            et.setText(li.getId());
        }
    }

    public void setListRequestCode(int listRequestCode) {
        this.mListRequestCode = listRequestCode;
    }

    public void setListItems(List<PopupListActivity.ListItem> listItems) {
        if (mListItems == null) return;
        this.mListItems.addAll(listItems);
        if (mRadio) {
            llRadio.removeAllViews();
            for (PopupListActivity.ListItem li : mListItems) {
                TextView tv = new TextView(getContext());
                tv.setText(li.getValue());
                tv.setTag(li.getId());
                tv.setCompoundDrawablesWithIntrinsicBounds(radioUnchecked, null, null, null);
                tv.setClickable(true);
                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (TextView tv : mRadios) {
                            if (tv == v) {
                                mRadioValue = tv.getTag().toString();
                                tv.setCompoundDrawablesWithIntrinsicBounds(radioChecked, null, null, null);
                            } else {
                                tv.setCompoundDrawablesWithIntrinsicBounds(radioUnchecked, null, null, null);
                            }
                        }
                        validate(false);
                    }
                });
                llRadio.addView(tv);
                mRadios.add(tv);
                mRadioValue = null;
            }
        }
    }

    public enum TipoDato {
        None,
        Testo,
        Numerico,
        Lista,
        Booleano,
        Data,
        Radio
    }

}
