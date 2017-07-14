package com.lsl.base.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;

public class BaseBeanParser<T> implements Parser {
	private Gson gson = new Gson();
//要将需要获取类型的 泛型类 作为TypeToken的 泛型参数构造一个匿名的子类，
// 就可以通过getType()方法获取到我们使用的泛型类的 泛型参数 类型。
	private TypeToken<T> token;
	
	public BaseBeanParser(TypeToken<T> token){
		this.token = token;
	}



	@Override
	public T parse(byte[] data) throws ParseException {
		try {
			String s = new String(data, "UTF-8");//将返回的数据格式化为UTF-8的格式
			System.out.println("接口返回:"+s);//打印要解析的数据
	        Type objectType = token.getType();  //返回值得类型，比如返回BaseBean<List<Person>>
	        return gson.fromJson(s, objectType);  //将数据解析成要的到的返回值类型，即传入的泛型T
		} catch (Exception e) {
			e.printStackTrace();
			throw new ParseException(e.getMessage(), 0);
		}
	}
}
