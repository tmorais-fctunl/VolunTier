package pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat;

public interface LikeClickEventInterface {
    void onLikeButtonClickListener(int position);
    void onEditButtonClickListener(int position, String comment);
    void onDeleteButtonClickListener(int position);
}
