package cn.w_wei.groupon.util;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtil {
    /**
     * 利用pinyin4j将参数中的中文转换为对应的汉语拼音
     * @param name
     * @return
     */
    public static String getPinyin(String name){
        try {
            String result = null;
            //1.设定汉语拼音的格式，设定为大写，不要声调
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            //2.根据设定好的格式，逐字进行汉字到拼音的转换
            char c;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < name.length(); i++) {
                c = name.charAt(i);
                //有多音字时，会返回多个拼音,只能翻译汉字，当遇到非汉字时，会报错
                String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if(pinyin.length > 0) {
                    sb.append(pinyin[0]);
                }
            }
            result = sb.toString();
            return result;
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();

            throw new RuntimeException("不正确的汉语拼音格式！");
        }
    }

    public static char getLetter(String name) {
        String pinyin = getPinyin(name);
        if(TextUtils.isEmpty(pinyin)){
            return 0;
        }else{
            return pinyin.charAt(0);
        }
    }
}
