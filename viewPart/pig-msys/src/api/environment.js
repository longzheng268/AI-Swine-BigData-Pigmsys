import myaxios from "../utils/myaxios";

/**
 * 环境监测数据 API
 */

// 获取所有环境数据
export function getEnvironmentList() {
  return myaxios.get("/environment/list");
}

// 分页查询环境数据
export function searchEnvironmentData(page, size, params) {
  return myaxios.post(`/environment/list/search/${page}/${size}`, null, {
    params,
  });
}

// 添加环境数据
export function addEnvironmentData(data) {
  return myaxios.post("/environment/add", data);
}

// 更新环境数据
export function updateEnvironmentData(id, data) {
  return myaxios.put(`/environment/${id}`, data);
}

// 删除环境数据
export function deleteEnvironmentData(id) {
  return myaxios.delete(`/environment/${id}`);
}

// 获取最新监测数据
export function getLatestData(limit = 10) {
  return myaxios.get("/environment/latest", { params: { limit } });
}

// 统计异常数据
export function countAbnormal(startTime, endTime) {
  return myaxios.get("/environment/abnormal/count", {
    params: { startTime, endTime },
  });
}
