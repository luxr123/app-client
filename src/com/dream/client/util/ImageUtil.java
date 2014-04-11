
package com.dream.client.util;

import com.dream.client.Config;

/**     
 * Simple to Introduction
 * @ProjectName:  [app-client]
 * @Package:      [com.dream.client.util]
 * @ClassName:    [ImageUtil]
 * @Description:  [一句话描述该类的功能]
 * @Author:       [xiaorui.lu]
 * @CreateDate:   [2014年4月9日 下午9:40:03]
 * @UpdateUser:   [xiaorui.lu]
 * @UpdateDate:   [2014年4月9日 下午9:40:03]
 * @UpdateRemark: [说明本次修改内容]
 * @Version:      [v1.0]
 *      
 */
public class ImageUtil {
	
	public static final String GETORIGINALPIC = "getOriginalPic";
	public static final String GETCOMPRESSPIC = "getCompressPic";

	public static String concatUrl(String path) {
		return Config.SERVER3 + GETCOMPRESSPIC+"?url=" + path;
	}
}

