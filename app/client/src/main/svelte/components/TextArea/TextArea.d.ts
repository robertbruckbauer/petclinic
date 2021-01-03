import { SvelteTypedComponent, SvelteAllProps } from 'svelte-typed-component';
export default class TextArea extends SvelteTypedComponent<TextAreaProps, TextAreaEvents, TextAreaSlots> {
}

declare const _TextAreaProps: {

    /** 
     * Disabled state.
     */
    disabled?: boolean;

    /** 
     * Input label
     */
    label?: string;

    /** 
     * Input Value.
     */
    value?: string;

    /**
     * Title for tooltip
     */
    title?: string

} & SvelteAllProps;

declare const _TextAreaEvents: {
    change: Event;
    input: Event;
    click: MouseEvent;
    focus: FocusEvent;
    blur: FocusEvent;
};

declare const _TextAreaSlots: {
};

export declare type TextAreaProps = typeof _TextAreaProps;
export declare type TextAreaEvents = typeof _TextAreaEvents;
export declare type TextAreaSlots = typeof _TextAreaSlots;
export {
};
