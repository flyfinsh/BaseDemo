package com.meilicat.basedemo.base;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.factory.ThreadPoolProxyFactory;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.UIUtils;

/**
 *
 * 这是控制数据的加载
 * 视图的展示逻辑
 * 控制大的流程，不关心具体的细节实现的类
 */
public abstract class LoadingPager extends FrameLayout{

    private View mLoadingView;
    private View			mErrorView;
    private View			mEmptyView;
    public View			mSuccessView;

    public static final int	STATE_NONE		= -1;				// 默认视图-->显示一个加载的效果
    public static final int	STATE_LOADING	= 0;				// 加载中-->表示正在加载数据
    public static final int	STATE_EMPTY		= 1;				// 空视图
    public static final int	STATE_ERROR		= 2;				// 错误视图
    public static final int	STATE_SUCCESS	= 3;				// 成功视图

    public int				mCurState		= STATE_NONE;	// 默认显示loading视图

    private Context mContext;

    public LoadingPager(Context context) {
        super(context);
        mContext = context;
        initCommonView();
    }

    /**
     * @des 初始化常规视图(加载视图, 空视图, 错误视图), 因为他们相对比较静态
     * @call LoadingPager初始化的时候被调用
     */
    private void initCommonView() {
        // 加载视图
        mLoadingView = View.inflate(mContext, R.layout.pager_loading, null);
//        TextView view = new TextView(UIUtils.getContext());
        addView(mLoadingView);
         //TODO: 2016/1/20 设置四种视图
         //错误页面
        mErrorView = View.inflate(mContext, R.layout.pager_error, null);
        addView(mErrorView);


        mErrorView.findViewById(R.id.error_btn_retry).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //希望重新的触发加载数据
                triggerLoadData();
            }
        });


        // 空页面
        mEmptyView = View.inflate(mContext, R.layout.pager_empty, null);

        addView(mEmptyView);

         //根据当前的状态,显示其中某一个视图
        refreshUIByState();
    }

    /**
     * @des 根据当前的状态, 显示其中某一个视图
     * @call 1.LoadingPager初始化的时候被调用
     * @call 2.数据开始加载之前,显示加载中的视图
     * @call 3.数据加载完成,根据具体结果,展示不同的数据
     */
    public void refreshUIByState() {
        // 控制loading视图的显示/隐藏
        mLoadingView.setVisibility((mCurState == STATE_LOADING)||(mCurState == STATE_NONE) ? View.VISIBLE : View.GONE);

        // 控制empty视图的显示/隐藏
        mEmptyView.setVisibility((mCurState == STATE_EMPTY) ? View.VISIBLE : View.GONE);

        // 控制错误视图的显示/隐藏
        mErrorView.setVisibility((mCurState == STATE_ERROR) ? View.VISIBLE : View.GONE);

//         如果是数据加载完成之后来到这里,那么就可能有成功视图了吧.
        if (mCurState == STATE_SUCCESS && mSuccessView == null) {
            // 初始化成功视图
            mSuccessView = initSuccessView();
            // 加入容器
            LogUtils.i("测试，这里表示加载数据成功");
            addView(mSuccessView);
        }

        // 控制成功视图的显示/隐藏
        if (mSuccessView != null) {
            LogUtils.i("测试，这里表示mSuccessView不为空");
            mSuccessView.setVisibility((mCurState == STATE_SUCCESS) ? View.VISIBLE : View.GONE);
        }

    }


    /**
     * @des 触发进行数据的加载
     * @call 想要加载数据的时候调用此方法
     */
    public void triggerLoadData() {
        // 如果当前状态是成功状态就无需加载
        if (mCurState != STATE_SUCCESS && mCurState != STATE_LOADING) {
            // 重置mCurState为loading状态
            mCurState = STATE_LOADING;
            // 根据当前的状态刷新ui
            refreshUIByState();


            ThreadPoolProxyFactory.createNormalThreadPoolProxy().execute(new LoadDataTask());
        }
    }

    class LoadDataTask implements Runnable {
        @Override
        public void run() {
            //TODO 测试加载中效果，睡了3s
//            SystemClock.sleep(3000);
            // 真正的在子线程里面开始加载想加载的数据
            LoadedResult tempState = initData();

            // 处理网络加载之后的结果
            mCurState = tempState.getState();

            UIUtils.postTaskSafely(new Runnable() {
                @Override
                public void run() {
                    // 刷新ui
                    refreshUIByState();
                }
            });
        }
    }


    /**
     * @return
     * @des 真正的在子线程里面开始加载想加载的数据
     * @des 不知道具体如何加载数据
     * @des 必须实现, 但是不知道具体实现, 定义成为抽象方法, 交给子类具体实现
     * @call 想要加载数据的时候
     */
    public abstract LoadedResult initData();

    /**
     * @return
     * @des 展示具体的成功视图
     * @des 此时不知道具体成功视图是啥
     * @des 必须实现, 但是不知道具体实现, 定义成为抽象方法, 交给子类具体实现
     * @call 数据加载完成, 并且是数据加载成功的时候
     */
    public abstract View initSuccessView();

   /**
    * 定义的枚举，用于表示加载的几种结果
    * */
    public enum LoadedResult {
        EMPTY(STATE_EMPTY), ERROR(STATE_ERROR), SUCCESS(STATE_SUCCESS);
        int	state;

        public int getState() {
            return state;
        }

        private LoadedResult(int state) {
            this.state = state;
        }
    }
}
