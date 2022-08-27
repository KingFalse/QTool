package cc.hicore.qtool.StickerPanelPlus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.hicore.Utils.FileUtils;
import cc.hicore.qtool.HookEnv;

public class LocalDataHelper {
    public static class LocalPath{
        public String coverName;
        public String Name;
        public String storePath;
    }
    public static List<LocalPath> readPaths(){
        try {
            String pathSetDir = HookEnv.ExtraDataPath + "本地表情包/set.json";
            JSONObject pathJson = new JSONObject(FileUtils.ReadFileString(pathSetDir));
            List<LocalPath> paths = new ArrayList<>();
            JSONArray pathList = pathJson.getJSONArray("paths");
            for (int i = 0; i < pathList.length(); i++) {
                JSONObject path = pathList.getJSONObject(i);
                LocalPath localPath = new LocalPath();
                localPath.coverName = path.getString("coverName");
                localPath.Name = path.getString("Name");
                localPath.storePath = path.getString("storePath");
                paths.add(localPath);
            }
            return paths;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public static class LocalPicItems{
        public String MD5;
        public String fileName;
        public String url;
        public long addTime;
        public int type;
    }
    public static List<LocalPicItems> getPicItems(String pathName){
        try {
            String pathSetDir = HookEnv.ExtraDataPath + "本地表情包/" + pathName + "/info.json";
            JSONObject pathJson = new JSONObject(FileUtils.ReadFileString(pathSetDir));
            List<LocalPicItems> items = new ArrayList<>();
            JSONArray pathList = pathJson.getJSONArray("items");
            for (int i = 0; i < pathList.length(); i++) {
                JSONObject path = pathList.getJSONObject(i);
                LocalPicItems localPath = new LocalPicItems();
                localPath.MD5 = path.getString("MD5");
                localPath.fileName = path.getString("fileName");
                localPath.addTime = path.getLong("addTime");
                localPath.type = path.getInt("type");
                localPath.url = path.getString("url");
                items.add(localPath);
            }
            return items;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public static boolean addPath(LocalPath addInfo){
        try {
            String pathSetDir = HookEnv.ExtraDataPath + "本地表情包/set.json";
            if (!new File(pathSetDir).exists()){
                FileUtils.WriteToFile(pathSetDir, "{\"paths\":[]}");
            }
            JSONObject pathJson = new JSONObject(FileUtils.ReadFileString(pathSetDir));
            JSONArray pathList = pathJson.getJSONArray("paths");
            for (int i = 0; i < pathList.length(); i++) {
                JSONObject path = pathList.getJSONObject(i);
                if(path.getString("Name").equals(addInfo.Name)){
                    return false;
                }
            }
            JSONObject newPath = new JSONObject();
            newPath.put("coverName", addInfo.coverName);
            newPath.put("Name", addInfo.Name);
            newPath.put("storePath", addInfo.storePath);
            pathList.put(newPath);

            new File(HookEnv.ExtraDataPath + "本地表情包/" + addInfo.storePath).mkdirs();
            FileUtils.WriteToFile(pathSetDir, pathJson.toString());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public static boolean addPicItem(String pathName, LocalPicItems addInfo){
        try {
            String pathSetDir = HookEnv.ExtraDataPath + "本地表情包/" + pathName + "/info.json";
            if (!new File(pathSetDir).exists()) {
                JSONObject pathJson = new JSONObject();
                pathJson.put("items", new JSONArray());
                FileUtils.WriteToFile(pathSetDir, pathJson.toString());
            }
            JSONObject pathJson = new JSONObject(FileUtils.ReadFileString(pathSetDir));
            JSONArray pathList = pathJson.getJSONArray("items");
            for (int i = 0; i < pathList.length(); i++) {
                JSONObject path = pathList.getJSONObject(i);
                if(path.getString("MD5").equals(addInfo.MD5)){
                    return false;
                }
            }
            JSONObject newPath = new JSONObject();
            newPath.put("MD5", addInfo.MD5);
            newPath.put("fileName", addInfo.fileName);
            newPath.put("addTime", addInfo.addTime);
            newPath.put("type", addInfo.type);
            newPath.put("url", addInfo.url);
            pathList.put(newPath);

            FileUtils.WriteToFile(pathSetDir, pathJson.toString());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public static void deletePath(LocalPath pathInfo){
        try {
            String pathSetDir = HookEnv.ExtraDataPath + "本地表情包/set.json";
            JSONObject pathJson = new JSONObject(FileUtils.ReadFileString(pathSetDir));
            JSONArray pathList = pathJson.getJSONArray("paths");
            for (int i = 0; i < pathList.length(); i++) {
                JSONObject path = pathList.getJSONObject(i);
                if(path.getString("Name").equals(pathInfo.Name)){
                    pathList.remove(i);
                    break;
                }
            }
            FileUtils.WriteToFile(pathSetDir, pathJson.toString());
            FileUtils.deleteFile(new File(HookEnv.ExtraDataPath + "本地表情包/" + pathInfo.storePath));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void deletePicItem(LocalPath pathInfo,LocalPicItems item){
        try {
            String pathSetDir = HookEnv.ExtraDataPath + "本地表情包/" + pathInfo.storePath + "/info.json";
            JSONObject pathJson = new JSONObject(FileUtils.ReadFileString(pathSetDir));
            JSONArray pathList = pathJson.getJSONArray("items");
            for (int i = 0; i < pathList.length(); i++) {
                JSONObject path = pathList.getJSONObject(i);
                if(path.getString("MD5").equals(item.MD5)){
                    pathList.remove(i);
                    break;
                }
            }
            FileUtils.WriteToFile(pathSetDir, pathJson.toString());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void setPathCover(LocalPath pathInfo,LocalPicItems coverItem){
        try {
            String pathSetDir = HookEnv.ExtraDataPath + "本地表情包/set.json";
            JSONObject pathJson = new JSONObject(FileUtils.ReadFileString(pathSetDir));
            JSONArray pathList = pathJson.getJSONArray("paths");
            for (int i = 0; i < pathList.length(); i++) {
                JSONObject path = pathList.getJSONObject(i);
                if(path.getString("Name").equals(pathInfo.Name)){
                    path.put("coverName", coverItem.fileName);
                    break;
                }
            }
            FileUtils.WriteToFile(pathSetDir, pathJson.toString());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void updatePicItemInfo(LocalPath pathInfo,LocalPicItems newItemInfo){
        try {
            String pathSetDir = HookEnv.ExtraDataPath + "本地表情包/" + pathInfo.storePath + "/info.json";
            JSONObject pathJson = new JSONObject(FileUtils.ReadFileString(pathSetDir));
            JSONArray pathList = pathJson.getJSONArray("items");
            for (int i = 0; i < pathList.length(); i++) {
                JSONObject path = pathList.getJSONObject(i);
                if(path.getString("MD5").equals(newItemInfo.MD5)){
                    path.put("fileName", newItemInfo.fileName);
                    path.put("addTime", newItemInfo.addTime);
                    path.put("type", newItemInfo.type);
                    path.put("url", newItemInfo.url);
                    break;
                }
            }
            FileUtils.WriteToFile(pathSetDir, pathJson.toString());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getLocalItemPath(RecentStickerHelper.RecentItemInfo recentItemInfo){
        try {
            String pathSetDir = HookEnv.ExtraDataPath + "本地表情包/" + recentItemInfo.pathName + "/info.json";
            JSONObject pathJson = new JSONObject(FileUtils.ReadFileString(pathSetDir));
            JSONArray pathList = pathJson.getJSONArray("items");
            for (int i = 0; i < pathList.length(); i++) {
                JSONObject path = pathList.getJSONObject(i);
                if(path.getString("MD5").equals(recentItemInfo.MD5)){
                    return HookEnv.ExtraDataPath + "本地表情包/" + recentItemInfo.pathName + "/" + recentItemInfo.MD5;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getLocalItemPath(LocalPath pathInfo,LocalPicItems newItemInfo){
        try {
            String pathSetDir = HookEnv.ExtraDataPath + "本地表情包/" + pathInfo.storePath + "/info.json";
            JSONObject pathJson = new JSONObject(FileUtils.ReadFileString(pathSetDir));
            JSONArray pathList = pathJson.getJSONArray("items");
            for (int i = 0; i < pathList.length(); i++) {
                JSONObject path = pathList.getJSONObject(i);
                if(path.getString("MD5").equals(newItemInfo.MD5)){
                    return HookEnv.ExtraDataPath + "本地表情包/" + pathInfo.storePath + "/" + newItemInfo.MD5;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
