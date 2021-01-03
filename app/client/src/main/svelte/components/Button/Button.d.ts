import { SvelteTypedComponent, SvelteAllProps } from 'svelte-typed-component';
export default class Button extends SvelteTypedComponent<ButtonProps, ButtonEvents, ButtonSlots> {
}

declare const _ButtonProps: {

    /** 
     * Disabled state. 
     */
    disabled?: boolean;

    /** 
     * Outlined variant.
     */
    outlined?: boolean;

    /**
     * Title for tooltip
     */
    title?: string

} & SvelteAllProps;

declare const _ButtonEvents: {
    click: MouseEvent;
    mouseover: MouseEvent;
    focus: FocusEvent;
    blur: FocusEvent;
};

declare const _ButtonSlots: {
    default: {};
};

export declare type ButtonProps = typeof _ButtonProps;
export declare type ButtonEvents = typeof _ButtonEvents;
export declare type ButtonSlots = typeof _ButtonSlots;
export {
};
