var path = require('path');

module.exports = {
    entry: './flexWorkflow/WebContent/scripts/frontend/CreateApplication.js',
    output: {
        filename: 'CreateApplication.js',
        path: path.resolve(__dirname, './flexWorkflow/WebContent/scripts/dist')
    },
    module: {
        loaders: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                loader: "babel-loader",
                query: {
                    presets: ['es2015', 'react', 'stage-2']
                }
            }
        ]
    }
};
