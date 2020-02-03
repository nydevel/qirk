const path = require("path");
const webpack = require("webpack");
const MiniCssExtractPluign = require("mini-css-extract-plugin");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const OptimizeCSSPlugin = require("optimize-css-assets-webpack-plugin");
const FriendlyErrorsPlugin = require("friendly-errors-webpack-plugin");
const CleanWebpackPlugin = require("clean-webpack-plugin");
const CompressionPlugin = require("compression-webpack-plugin");
const autoprefixer = require("autoprefixer");
const packageConfig = require("./package.json");

const localDevelopmentEnv = require("./local-development-env");
const developmentEnv = require("./development-env");
const stageEnv = require("./stage-env");
const productionEnv = require("./production-env");
const commonEnv = require("./common-env");

module.exports = (env, options) => {
  const localModes = ["local-development"];
  const mode = env.ENV_TYPE;

  const localMode = localModes.some(m => m === mode);

  let currentEnv = localDevelopmentEnv;

  if (mode === "stage") {
    currentEnv = stageEnv;
  } else if (mode === "development") {
    currentEnv = developmentEnv;
  } else if (mode === "production") {
    currentEnv = productionEnv;
  } // Else - as initially defined

  currentEnv = { ...commonEnv, ...currentEnv };

  const createNotifierCallback = () => {
    const notifier = require("node-notifier");

    return (severity, errors) => {
      if (severity !== "error") {
        return;
      }

      const error = errors[0];
      const filename = error.file && error.file.split("!").pop();

      notifier.notify({
        title: packageConfig.name,
        message: severity + ": " + error.name,
        subtitle: filename || ""
      });
    };
  };

  function resolve(dir) {
    return path.resolve(__dirname, dir);
  }

  const assetsPath = function(_path) {
    return path.posix.join("static", _path);
  };

  const createLintingRule = () => ({
    test: /\.(jsx)$/,
    loader: "eslint-loader",
    enforce: "pre",
    include: [resolve("src"), resolve("static")],
    options: {
      formatter: require("eslint-friendly-formatter"),
      emitWarning: true
    }
  });

  const devServer = {
    clientLogLevel: "warning",
    historyApiFallback: true,
    hot: true,
    contentBase: false, // since we use CopyWebpackPlugin.
    host: "localhost",
    port: 8080,
    overlay: { warnings: false, errors: true },
    publicPath: "/",
    quiet: true // necessary for FriendlyErrorsPlugin
  };

  const localPlugins = [
    new webpack.HotModuleReplacementPlugin(),
    new webpack.NamedModulesPlugin(), // HMR shows correct file names in console on update.
    new webpack.NoEmitOnErrorsPlugin(),
    new webpack.EnvironmentPlugin(currentEnv),
    new HtmlWebpackPlugin({
      filename: "index.html",
      template: "static/template.html",
      serverUrl: currentEnv.REACT_APP_SERVER_URL,
      apiPrefix: currentEnv.REACT_APP_API_PREFIX,
      version: currentEnv.VERSION,
      mode: currentEnv.MODE,
      gRecaptchaSiteKey: currentEnv.REACT_APP_SITE_KEY_FOR_GRECAPTCHA
    }),
    new CopyWebpackPlugin([
      {
        from: resolve("static"),
        ignore: ["template.html"]
      }
    ]),
    new FriendlyErrorsPlugin({
      compilationSuccessInfo: {
        messages: ["Your application is running here: http://localhost:9090"]
      },
      onErrors: createNotifierCallback()
    })
  ];

  const buildPlugins = [
    new webpack.EnvironmentPlugin(currentEnv),
    new CleanWebpackPlugin(),
    new MiniCssExtractPluign({
      filename: assetsPath("css/[name].[chunkHash].css")
    }),
    new OptimizeCSSPlugin({
      cssProcessorOptions: { safe: true, map: { inline: false } }
    }),
    new HtmlWebpackPlugin({
      filename: "index.html",
      template: "static/template.html",
      serverUrl: currentEnv.REACT_APP_SERVER_URL,
      apiPrefix: currentEnv.REACT_APP_API_PREFIX,
      version: currentEnv.VERSION,
      mode: currentEnv.MODE,
      gRecaptchaSiteKey: currentEnv.REACT_APP_SITE_KEY_FOR_GRECAPTCHA
    }),
    new webpack.HashedModuleIdsPlugin(),
    new webpack.optimize.ModuleConcatenationPlugin(),
    new CopyWebpackPlugin([
      {
        from: resolve("static"),
        ignore: ["template.html"]
      }
    ]),
    new CompressionPlugin({
      filename: "[path].gz[query]",
      algorithm: "gzip",
      test: /\.js$|\.css$|\.css$/,
      threshold: 10240,
      minRatio: 0.8
    })
  ];

  const plugins = localMode ? localPlugins : buildPlugins;

  const output = localMode
    ? {
        path: resolve("build"),
        filename: "[name].js",
        publicPath: "/"
      }
    : {
        path: resolve("build"),
        filename: assetsPath("js/[name].[chunkhash].js"),
        chunkFilename: assetsPath("js/[id].[chunkhash].js"),
        publicPath: "/"
      };

  return {
    entry: {
      app: "./src/index.js"
    },
    output,
    resolve: {
      extensions: [".js", ".json", ".jsx"]
    },
    devServer,
    devtool: "source-map",
    module: {
      rules: [
        createLintingRule(),
        {
          test: /\.jsx?$/,
          loader: "babel-loader",
          exclude: /node-modules/
        },
        {
          test: /\.(c|sa)ss$/,
          use: [
            localMode ? "style-loader" : MiniCssExtractPluign.loader,
            "css-loader",
            {
              loader: "postcss-loader",
              options: {
                plugins: [autoprefixer()],
                sourceMap: true
              }
            },
            {
              loader: "sass-loader",
              options: {
                includePaths: ["./node_modules"]
              }
            }
          ]
        },
        {
          test: /\.(png|jpe?g|gif|svg)(\?.*)?$/,
          loader: "url-loader",
          options: {
            limit: 10000,
            name: assetsPath("img/[name].[ext]")
          }
        },
        {
          test: /\.(mp4|webm|ogg|mp3|wav|flac|aac)(\?.*)?$/,
          loader: "url-loader",
          options: {
            limit: 10000,
            name: assetsPath("media/[name].[hash:7].[ext]")
          }
        },
        {
          test: /\.(woff2?|eot|ttf|otf)(\?.*)?$/,
          loader: "url-loader",
          options: {
            limit: 10000,
            name: assetsPath("fonts/[name].[hash:7].[ext]")
          }
        }
      ]
    },
    plugins,
    optimization: {
      namedChunks: true,
      namedModules: true,
      minimize: true,
      splitChunks: {
        minSize: 0,
        chunks: "all",
        maxInitialRequests: Infinity,
        cacheGroups: {
          vendor: {
            test: /[\\/]node_modules[\\/]/,
            name: "vendor",
            enforce: true
          }
        }
      }
    }
  };
};
