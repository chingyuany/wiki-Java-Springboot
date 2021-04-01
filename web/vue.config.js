//設置這個網站標題
module.exports = {
    chainWebpack: config => {
        config
            .plugin('html')
            .tap(args => {
                args[0].title= 'Alan Wiki'
                return args
            })
    },
// //    如果要換icon的話
//     pwa: {
//         iconPaths: {
//             favicon32: 'favicon.ico',
//             favicon16: 'favicon.ico',
//             appleTouchIcon: 'favicon.ico',
//             maskIcon: 'favicon.ico',
//             msTileImage: 'favicon.ico'
//         }
//     }

}
