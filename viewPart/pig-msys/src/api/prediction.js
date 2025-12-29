import myaxios from "../utils/myaxios";

/**
 * 预测分析 API
 */

// 获取所有模型
export function getAllModels() {
  return myaxios.get("/prediction/models");
}

// 按类型获取模型
export function getModelsByType(type) {
  return myaxios.get(`/prediction/models/type/${type}`);
}

// 获取公开模型
export function getPublicModels() {
  return myaxios.get("/prediction/models/public");
}

// 获取模型详情
export function getModelById(id) {
  return myaxios.get(`/prediction/models/${id}`);
}

// 执行预测
export function predict(modelId, inputData, userId = 1) {
  return myaxios.post("/prediction/predict", inputData, {
    params: { modelId, userId },
  });
}

// 获取所有预测记录
export function getAllPredictionRecords() {
  return myaxios.get("/prediction/records");
}

// 按用户获取预测记录
export function getPredictionRecordsByUser(userId) {
  return myaxios.get(`/prediction/records/user/${userId}`);
}

// 按模型获取预测记录
export function getPredictionRecordsByModel(modelId) {
  return myaxios.get(`/prediction/records/model/${modelId}`);
}
