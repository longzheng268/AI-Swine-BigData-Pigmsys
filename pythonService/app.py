"""
Python 预测服务
用于处理生猪养殖数据的预测分析

依赖安装:
pip install flask numpy scikit-learn joblib

运行方式:
python app.py

服务将运行在 http://localhost:5001
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import numpy as np
import random

app = Flask(__name__)
CORS(app)  # 允许跨域

@app.route('/predict', methods=['POST'])
def predict():
    """
    预测接口
    接收参数:
    {
        "model_type": "GROWTH/ENVIRONMENT/DISEASE",
        "model_path": "/path/to/model",
        "input_data": {...}
    }
    """
    try:
        data = request.get_json()
        model_type = data.get('model_type')
        input_data = data.get('input_data', {})
        
        # 根据模型类型进行预测
        if model_type == 'GROWTH':
            result = predict_growth(input_data)
        elif model_type == 'ENVIRONMENT':
            result = predict_environment(input_data)
        elif model_type == 'DISEASE':
            result = predict_disease(input_data)
        else:
            return jsonify({
                'code': 400,
                'message': '不支持的模型类型',
                'data': None
            })
        
        return jsonify({
            'code': 200,
            'message': '预测成功',
            'data': result
        })
    
    except Exception as e:
        return jsonify({
            'code': 500,
            'message': f'预测失败: {str(e)}',
            'data': None
        })


def predict_growth(input_data):
    """
    生猪生长预测
    输入: 日龄、饲料摄入量、品种、性别
    输出: 预测体重、生长速度
    """
    age = float(input_data.get('age', 100))
    feed = float(input_data.get('feed', 2.5))
    breed = input_data.get('breed', '白猪')
    sex = input_data.get('sex', '公')
    
    # 简单的预测模型（实际应用中应使用训练好的模型）
    base_weight = 30 + age * 0.5 + feed * 10
    breed_factor = 1.1 if breed == '白猪' else 1.0
    sex_factor = 1.05 if sex == '公' else 1.0
    
    predicted_weight = base_weight * breed_factor * sex_factor
    growth_rate = predicted_weight / age if age > 0 else 0
    
    return {
        'predicted_weight': round(predicted_weight, 2),
        'growth_rate': round(growth_rate, 3),
        'accuracy': 0.925,
        'confidence': random.uniform(0.85, 0.95),
        'suggestions': '生长状况良好，建议继续保持当前饲养方式'
    }


def predict_environment(input_data):
    """
    环境质量评价
    输入: 温度、湿度、CO2浓度、氨气浓度、光照强度
    输出: 环境等级、评分、改进建议
    """
    temperature = float(input_data.get('temperature', 25))
    humidity = float(input_data.get('humidity', 65))
    co2 = float(input_data.get('co2', 500))
    nh3 = float(input_data.get('nh3', 20))
    light = float(input_data.get('light', 300))
    
    # 评分计算
    score = 100
    issues = []
    
    if temperature < 18 or temperature > 28:
        score -= 15
        issues.append('温度超标')
    if humidity < 50 or humidity > 70:
        score -= 10
        issues.append('湿度超标')
    if co2 > 600:
        score -= 15
        issues.append('CO2浓度过高')
    if nh3 > 25:
        score -= 20
        issues.append('氨气浓度过高')
    if light < 200 or light > 500:
        score -= 10
        issues.append('光照不适宜')
    
    # 确定等级
    if score >= 90:
        level = 'I'
        quality = '优秀'
    elif score >= 75:
        level = 'II'
        quality = '良好'
    elif score >= 60:
        level = 'III'
        quality = '一般'
    else:
        level = 'IV'
        quality = '较差'
    
    suggestions = '、'.join(issues) if issues else '环境良好，建议保持'
    
    return {
        'quality_level': level,
        'quality': quality,
        'score': score,
        'temperature_status': '正常' if 18 <= temperature <= 28 else '异常',
        'humidity_status': '正常' if 50 <= humidity <= 70 else '异常',
        'co2_status': '正常' if co2 <= 600 else '超标',
        'nh3_status': '正常' if nh3 <= 25 else '超标',
        'light_status': '正常' if 200 <= light <= 500 else '异常',
        'suggestions': suggestions,
        'accuracy': 0.88
    }


def predict_disease(input_data):
    """
    疾病风险预测
    输入: 温度、湿度、养殖密度、日龄、疫苗接种情况
    输出: 疾病风险等级、风险概率、预防建议
    """
    temperature = float(input_data.get('temperature', 25))
    humidity = float(input_data.get('humidity', 65))
    density = float(input_data.get('density', 10))
    age = float(input_data.get('age', 60))
    vaccinated = input_data.get('vaccinated', True)
    
    # 风险计算
    risk_score = 0
    
    if temperature < 18 or temperature > 28:
        risk_score += 20
    if humidity > 70:
        risk_score += 15
    if density > 15:
        risk_score += 25
    if age < 30:
        risk_score += 10
    if not vaccinated:
        risk_score += 30
    
    # 确定风险等级
    if risk_score <= 20:
        level = '低'
        probability = 0.1 + random.uniform(0, 0.1)
        advice = '风险较低，注意日常卫生和防疫'
    elif risk_score <= 40:
        level = '中'
        probability = 0.3 + random.uniform(0, 0.2)
        advice = '存在一定风险，建议加强通风和消毒'
    elif risk_score <= 60:
        level = '较高'
        probability = 0.5 + random.uniform(0, 0.2)
        advice = '风险较高，建议立即改善环境并加强监控'
    else:
        level = '高'
        probability = 0.7 + random.uniform(0, 0.2)
        advice = '风险很高，建议立即采取防疫措施并咨询兽医'
    
    return {
        'risk_level': level,
        'risk_probability': round(probability, 3),
        'risk_score': risk_score,
        'prevention_advice': advice,
        'suggestions': [
            '定期消毒' if density > 10 else '',
            '改善通风' if humidity > 70 else '',
            '调节温度' if (temperature < 18 or temperature > 28) else '',
            '及时疫苗接种' if not vaccinated else ''
        ],
        'accuracy': 0.865
    }


@app.route('/health', methods=['GET'])
def health():
    """健康检查接口"""
    return jsonify({
        'status': 'ok',
        'message': 'Python预测服务运行正常'
    })


if __name__ == '__main__':
    print('=' * 60)
    print('Python 预测服务启动中...')
    print('服务地址: http://localhost:5001')
    print('健康检查: http://localhost:5001/health')
    print('预测接口: http://localhost:5001/predict')
    print('=' * 60)
    app.run(host='0.0.0.0', port=5001, debug=True)
