import myaxios from "../utils/myaxios";

/**
 * 数据上传 API
 */

// 上传环境监测数据
export function uploadEnvironmentData(file, userId, username) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('userId', userId);
  formData.append('username', username);
  
  return myaxios.post('/upload/environment', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

// 获取所有上传记录
export function getUploadRecords() {
  return myaxios.get('/upload/records');
}

// 按用户获取上传记录
export function getUploadRecordsByUser(userId) {
  return myaxios.get(`/upload/records/${userId}`);
}



