import { SvelteTypedComponent, SvelteAllProps } from 'svelte-typed-component';
export default class Goto extends SvelteTypedComponent<GotoProps, GotoEvents, GotoSlots> {
}

declare const _GotoProps: {

    /** 
     * Disabled state. 
     */
    disabled?: boolean;

    /** 
     * Material icon name.
     */
    icon?: string;

    /** 
     * Page target.
     */
    page?: string;

    /** 
     * Outlined variant.
     */
    outlined?: boolean;

    /**
     * Title for tooltip
     */
    title?: string

} & SvelteAllProps;

declare const _GotoEvents: {
    click: MouseEvent;
    mouseover: MouseEvent;
    focus: FocusEvent;
    blur: FocusEvent;
};

declare const _GotoSlots: {
};

export declare type GotoProps = typeof _GotoProps;
export declare type GotoEvents = typeof _GotoEvents;
export declare type GotoSlots = typeof _GotoSlots;
export {
};