package net.oschina.gitapp.media;


interface Contract {
    interface Presenter {
        void requestCamera();

        void requestExternalStorage();

        void setDataView(View view);
    }

    interface View {

        void onOpenCameraSuccess();

        void onCameraPermissionDenied();
    }
}
