import { SvelteTypedComponent, SvelteAllProps } from 'svelte-typed-component';

export default class TextField extends SvelteTypedComponent<TextFieldProps, TextFieldEvents, TextFieldSlots> {
}

declare const _TextFieldProps: {

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

declare const _TextFieldEvents: {
    blur: FocusEvent;
    change: Event;
    input: Event;
    click: MouseEvent;
    focus: FocusEvent;
};

declare const _TextFieldSlots: {
};

export declare type TextFieldProps = typeof _TextFieldProps;
export declare type TextFieldEvents = typeof _TextFieldEvents;
export declare type TextFieldSlots = typeof _TextFieldSlots;
export {
};
