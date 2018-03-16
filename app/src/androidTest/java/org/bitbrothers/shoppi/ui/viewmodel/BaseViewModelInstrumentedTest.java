package org.bitbrothers.shoppi.ui.viewmodel;


import android.support.test.runner.AndroidJUnit4;

import org.bitbrothers.shoppi.logging.Logger;
import org.bitbrothers.shoppi.logging.NullLogger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(AndroidJUnit4.class)
public class BaseViewModelInstrumentedTest {

    public static class TestableBaseViewModel extends BaseViewModel<BaseViewModel.BaseView> {

        public TestableBaseViewModel(Logger logger) {
            super(logger);
        }

        @Override
        protected void addViewDisposable(Disposable disposable) {
            super.addViewDisposable(disposable);
        }

        @Override
        protected void withView(Consumer<BaseView> action) {
            super.withView(action);
        }
    }

    public static class TestableView implements BaseViewModel.BaseView {

        @Override
        public void showErrorToast(int messageResId) {
        }

        @Override
        public void showErrorToast(int formatMessageResId, Object... args) {
        }
    }

    @Test
    public void withView_executeActionWhileDetached_shouldExecuteActionWhenAttached() throws Exception {
        TestableBaseViewModel viewModel = new TestableBaseViewModel(new NullLogger());
        TestableView dummyView = new TestableView();
        Consumer<BaseViewModel.BaseView> consumer = mock(Consumer.class);
        ArgumentCaptor<BaseViewModel.BaseView> captor = ArgumentCaptor.forClass(BaseViewModel.BaseView.class);

        viewModel.withView(consumer);

        verifyZeroInteractions(consumer);

        viewModel.attach(dummyView);

        verify(consumer, times(1)).accept(captor.capture());
        assertThat(captor.getValue(), is(dummyView));
    }

    @Test
    public void withView_executeActionWhileAttached_shouldExecuteActionSynchronously() throws Exception {
        TestableBaseViewModel viewModel = new TestableBaseViewModel(new NullLogger());
        TestableView dummyView = new TestableView();
        Consumer<BaseViewModel.BaseView> consumer = mock(Consumer.class);
        ArgumentCaptor<BaseViewModel.BaseView> captor = ArgumentCaptor.forClass(BaseViewModel.BaseView.class);

        viewModel.attach(dummyView);
        viewModel.withView(consumer);

        verify(consumer, times(1)).accept(captor.capture());
        assertThat(captor.getValue(), is(dummyView));
    }

    @Test
    public void addViewDisposable_shouldDisposeWhenViewDetaches() {
        TestableBaseViewModel viewModel = new TestableBaseViewModel(new NullLogger());
        TestableView dummyView = new TestableView();
        Disposable disposable = mock(Disposable.class);

        viewModel.attach(dummyView);
        viewModel.addViewDisposable(disposable);
        viewModel.detach();
        viewModel.attach(dummyView);
        viewModel.detach();

        verify(disposable, times(1)).dispose();
    }
}
