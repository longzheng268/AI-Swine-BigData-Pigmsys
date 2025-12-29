module.exports={
    // devServer:{
    //     open:true,
    //     https:false,
    //     host:'localhost',
    //     proxy:{
    //         [process.env.VUE_APP_BASE_API]:{//虚拟路径
    //             target:process.env.VUE_APP_SERVICE_URL,//代理目标
    //             changeOrigin: true,//开启代理
    //             pathRewrite: {   //替换虚拟路径  路径重写
    //                 // '^/dev-apis':''
    //                 ['^'+process.env.VUE_APP_BASE_API]:''
    //             }
    //         }
    //
    //     }
    // },

    devServer:{
        port: 8081,
        client: {
            webSocketURL: 'auto://0.0.0.0:0/ws'
        },
        proxy: {
            // 代理所有 API 请求到后端，但排除前端路由
            // 注意：使用精确路径匹配，避免前端路由被误代理
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            // 只代理 /user 下的 API 路径，不代理根路径 /user（前端路由）
            '/user/login': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            '/user/register': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            '/user/info': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            '/user/logout': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            '/auth': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            '/pig': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            '/pigInfo': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            '/personalInfo': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            '/environment': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            '/prediction': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            '/upload': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            '/userinfo': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            '/addUser': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            '/getVerify': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            },
            '/checkVerify': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                ws: false
            }
        },
        // 配置 history API 回退，确保前端路由正常工作
        historyApiFallback: {
            disableDotRule: true,
            htmlAcceptHeaders: ['text/html', 'application/xhtml+xml']
        }
    },
    lintOnSave:false,
    productionSourceMap:false
}
